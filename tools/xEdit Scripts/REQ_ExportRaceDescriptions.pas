unit ExportRaceDescriptions;

uses REQ_Util;

var
  output: TStringList;
  re_blood, re_heritage, re_physique, re_power, re_unperked: TPerlRegEx;


function Initialize: Integer;
begin
  output := TStringList.Create;

  re_blood := TPerlRegEx.Create;
  re_blood.RegEx := '^[^_]+_Trait_Blood_';

  re_heritage := TPerlRegEx.Create;
  re_heritage.RegEx := '^[^_]+_Trait_Heritage_';

  re_physique := TPerlRegEx.Create;
  re_physique.RegEx := '^[^_]+_Trait_Physique_';

  re_power := TPerlRegEx.Create;
  re_power.RegEx := '^[^_]+_Power_Race_';

  re_unperked := TPerlRegEx.Create;
  re_unperked.RegEx := '^[^_]+_RacialSkills_(.+)';
end;

function Process(e: IInterface): Integer;
var
  blood, heritage, physique, power, unperked, skill: String;
  health, magicka, stamina, carryWeight, unarmedDamage, magickaRate, staminaRate: Float;
  fullName: IInterface;
begin
  if Signature(e) <> 'RACE' then Exit;

  if GetElementNativeValues(e, 'DATA - DATA\Flags\Playable') = 0 then Exit;

  health := GetElementNativeValues(e, 'DATA - DATA\Starting Health');
  magicka := GetElementNativeValues(e, 'DATA - DATA\Starting Magicka');
  stamina := GetElementNativeValues(e, 'DATA - DATA\Starting Stamina');
  carryWeight := GetElementNativeValues(e, 'DATA - DATA\Base Carry Weight');
  unarmedDamage := GetElementNativeValues(e, 'DATA - DATA\Unarmed Damage');
  magickaRate := GetElementNativeValues(e, 'DATA - DATA\Magicka Regen');
  staminaRate := GetElementNativeValues(e, 'DATA - DATA\Stamina Regen');
  blood := GetSpellDescription(e, re_blood);
  physique := GetSpellDescription(e, re_physique);
  heritage := GetSpellDescription(e, re_heritage);
  power := GetSpellDescription(e, re_power);
  unperked := GetKeywords(e, re_unperked);
  skill := GetSkillBoosts(e);
  fullName := GetElementNativeValues(e, 'FULL - Name');

  output.Add(Format('%s,%.0f,%.0f,%.0f,%.0f,%g,%g,%g,"%s","%s","%s","%s",%s,%s', [fullName, health, magicka, stamina, carryWeight, unarmedDamage, magickaRate, staminaRate, blood, physique, heritage, power, unperked, skill]));
end;

function Finalize: Integer;
begin
  output.Sort;
  output.Insert(0, 'race,health,magicka,stamina,carry_weight,unarmed_damage,magicka_rate,stamina_rate,blood,physique,heritage,power,unperked,skill');
  // output.SaveToFile(DataPath + 'tools\xEdit Scripts\export\RaceDescriptions.csv');
  output.SaveToFile('C:\Skyrim Tools\Mod Organizer SSE\mods\requiem\tools\xEdit Scripts\export\RaceDescriptions.csv');
  output.Free;
  re_blood.Free;
  re_heritage.Free;
  re_physique.Free;
  re_power.Free;
  re_unperked.Free;
end;

function GetSpellDescription(e: IInterface; re: TPerlRegEx): String;
var
  i: Integer;
  description: String;
  spells, spell: IInterface;
begin
  Result := nil;
  spells := ElementByPath(e, 'Actor Effects');
  if not Assigned(spells) then
    Exit;
  for i := 0 to Pred(ElementCount(spells)) do begin
    spell := WinningOverride(LinksTo(ElementByIndex(spells, i))); 
    re.Subject := EditorID(spell);
    if re.Match then
      Result := Result + ' ' + GetEffectsDescription(spell);
  end;
  if Assigned(Result) then
    Delete(Result, 1, 1);
end;

function GetEffectsDescription(e: IInterface): String;
var
  effects, effect, magicEffect: IInterface;
  description: String;
  i, mag, dur: Integer;
begin
  Result := nil;
  effects := ElementByPath(e, 'Effects');
  if not Assigned(effects) then
    Exit;
  for i := 0 to Pred(ElementCount(effects)) do begin
    effect := ElementByIndex(effects, i);
    magicEffect := WinningOverride(LinksTo(ElementByPath(effect, 'EFID - Base Effect')));
    if GetElementNativeValues(magicEffect, 'Magic Effect Data\DATA - Data\Flags\Hide in UI') = -1 then
      Continue;
    description := GetElementNativeValues(magicEffect, 'DNAM - Magic Item Description');
    mag := GetElementNativeValues(effect, 'EFIT - EFIT\Magnitude');
    dur := GetElementNativeValues(effect, 'EFIT - EFIT\Duration');
    description := StringReplace(description, '<mag>', Format('<%d>', [mag]), rfReplaceAll);
    description := StringReplace(description, '<dur>', Format('<%d>', [dur]), rfReplaceAll);
    Result := Result + ' ' + description;
  end;
  if Assigned(Result) then
    Delete(Result, 1, 1);
end;

function GetKeywords(e: IInterface; re: TPerlRegEx): String;
var
  i: Integer;
  keywords, keyword: IInterface;
begin
  Result := nil;
  keywords := ElementByPath(e, 'KWDA - Keywords');
  if not Assigned(keywords) then
    Exit;
  for i := 0 to Pred(ElementCount(keywords)) do begin
    keyword := WinningOverride(LinksTo(ElementByIndex(keywords, i))); 
    re.Subject := EditorID(keyword);
    if re.Match then
      Result := Result + ';' + re.Groups[1];
  end;
  if Assigned(Result) then
    Delete(Result, 1, 1);
end;

function GetSkillBoosts(e: IInterface): String;
var
  i, boost: Integer;
  description, skill: String;
  skillBoosts, skillBoost: IInterface;
begin
  Result := nil;
  skillBoosts := ElementByPath(e, 'DATA - DATA\Skill Boosts');
  if not Assigned(skillBoosts) then
    Exit;
  for i := 0 to Pred(ElementCount(skillBoosts)) do begin
    skillBoost := ElementByIndex(skillBoosts, i);
    skill := GetElementEditValues(skillBoost, 'Skill');
    boost := GetElementNativeValues(skillBoost, 'Boost');
    if not SameText(skill, 'None') then
      Result := Result + ';' + Format('+%d %s', [boost, skill]);
  end;
  if Assigned(Result) then
    Delete(Result, 1, 1);
end;

end.
