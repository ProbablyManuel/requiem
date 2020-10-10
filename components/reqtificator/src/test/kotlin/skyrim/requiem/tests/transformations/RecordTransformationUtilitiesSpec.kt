package skyrim.requiem.tests.transformations

import Reqtificator.FormIDStash
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import skyproc.FLST
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyproc.ModListing
import skyproc.PERK
import skyrim.requiem.exceptions.InvalidDataInLoadOrderException
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.UrlReference
import skyrim.requiem.transformations.RecordTransformationUtilities

class RecordTransformationUtilitiesSpec : WordSpec() {
    init {

        class Fixture {
            val formIDs1 = arrayListOf(FormID("123456Skyrim.esm"), FormID("ABC456Skyrim.esm"))
            val formIDs2 = arrayListOf(FormID("123456Requiem.esp"), FormID("ABC456Requiem.esp"))
            val version1 = mockk<FLST>()
            val version2 = mockk<FLST>()
            val perkList = mockk<FLST>()
            val context = mockk<Mod>()
        }

        "A RecordTransformations" When {

            "loading globally distributed perks from the imported mods" should {
                "return a merged version of all imported versions" {
                    val f = Fixture()

                    every { f.context.getMajor(FormIDStash.formlist_GM_perks, GRUP_TYPE.FLST) } returns f.perkList
                    every { f.perkList.recordHistory } returns arrayListOf(f.version1, f.version2)
                    every { f.version1.formIDEntries } returns f.formIDs1
                    every { f.version2.formIDEntries } returns f.formIDs2
                    (f.formIDs1 + f.formIDs2).forEach {
                        every { f.context.getMajor(it, GRUP_TYPE.PERK) } returns mockk<PERK>()
                    }

                    RecordTransformationUtilities.loadMechanicsPerksFromPlugins(f.context) shouldContainExactlyInAnyOrder (
                        f.formIDs1 + f.formIDs2
                        )
                }

                "throw an exception if any of the specified records is not a perk" {
                    val f = Fixture()

                    every { f.context.getMajor(FormIDStash.formlist_GM_perks, GRUP_TYPE.FLST) } returns f.perkList
                    every { f.perkList.recordHistory } returns arrayListOf(f.version1, f.version2)
                    every { f.version1.formIDEntries } returns arrayListOf(f.formIDs1.first())
                    every { f.version1.modImportedFrom } returns ModListing("fail.esm")
                    every { f.version2.formIDEntries } returns arrayListOf()
                    every { f.context.getMajor(any<FormID>(), GRUP_TYPE.PERK) } returns null

                    val exception = shouldThrow<InvalidDataInLoadOrderException> {
                        RecordTransformationUtilities.loadMechanicsPerksFromPlugins(f.context)
                    }
                    exception.messageTemplate shouldBe TextReference(
                        "patch.actors.wrong_type_in_mechanics_perks_list",
                        listOf(f.formIDs1.first(), "fail.esm", UrlReference("urls.mechanics_perks"))
                        )
                }
            }
        }
    }
}