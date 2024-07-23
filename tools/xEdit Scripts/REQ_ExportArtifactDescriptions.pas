unit ExportArtifactDescription;

uses REQ_Util;

var
  output: TStringList;
  re_artifact, re_jewelry, re_armor, re_weapon, re_ench_apply, re_ench_equip_ability, re_number: TPerlRegEx;


function Initialize: Integer;
begin
  output := TStringList.Create;

  re_artifact := TPerlRegEx.Create;
  re_artifact.RegEx := '^[^_]+_Artifact_(.+)$';

  re_jewelry := TPerlRegEx.Create;
  re_jewelry.RegEx := '^[^_]+(?:_Var)?_(Amulet|Circlet|Ring)_([^_]+)_([^_]+)';

  re_armor := TPerlRegEx.Create;
  re_armor.RegEx := '^[^_]+(?:_Var)?_(Cloth|Heavy|Light)_([^_]+)_([^_]+)';

  re_weapon := TPerlRegEx.Create;
  re_weapon.RegEx := '^[^_]+(?:_Var)?_Weapon_([^_]+)_([^_]+)';

  re_ench_apply := TPerlRegEx.Create;
  re_ench_apply.RegEx := '^([^_]+_Ench_[^_]+)_Apply$';

  re_ench_equip_ability := TPerlRegEx.Create;
  re_ench_equip_ability.RegEx := '^([^_]+_Ench_[^_]+)_EquipAbility$';

  re_number := TPerlRegEx.Create;
  re_number.RegEx := '<[0-9]+>';
end;

function Process(e: IInterface): Integer;
var
  slot, description, fullName: String;
begin
  if (Signature(e) <> 'WEAP') and (Signature(e) <> 'ARMO') then Exit;

  re_artifact.Subject := EditorID(e);
  re_jewelry.Subject := EditorID(e);
  re_armor.Subject := EditorID(e);
  re_weapon.Subject := EditorID(e);
  if re_artifact.Match then
    slot := GetSlot(e)
  else if re_jewelry.Match then
    slot := re_jewelry.Groups[1]
  else if re_armor.Match then
    slot := re_armor.Groups[3]
  else if re_weapon.Match then
    slot := re_weapon.Groups[2]
  else
    Exit;
  fullName := GetElementNativeValues(e, 'FULL - Name');
  description := GetItemDescription(e);

  output.Add(Format('%s,%s,%s,"%s"', [EditorID(e), slot, fullName, description]));
end;

function Finalize: Integer;
begin
  output.Sort;
  output.SaveToFile(DataPath + 'tools\xEdit Scripts\export\ArtifactDescriptions.csv');
  output.Free;
  re_artifact.Free;
  re_jewelry.Free;
  re_armor.Free;
  re_weapon.Free;
  re_ench_apply.Free;
  re_ench_equip_ability.Free;
  re_number.Free;
end;

function GetSlot(e: IInterface): String;
begin
  if HasKeyword(e, 'ClothingCirclet') or HasKeyword(e, 'ClothingHead') or HasKeyword(e, 'ArmorHelmet') then
    Result := 'Head'
  else if HasKeyword(e, 'ClothingBody') or HasKeyword(e, 'ArmorCuirass') then
    Result := 'Body'
  else if HasKeyword(e, 'ClothingHands') or HasKeyword(e, 'ArmorGauntlets') then
    Result := 'Hands'
  else if HasKeyword(e, 'ClothingFeet') or HasKeyword(e, 'ArmorBoots') then
    Result := 'Feet'
  else if HasKeyword(e, 'ArmorShield') then
    Result := 'Shield'
  else if HasKeyword(e, 'ClothingNecklace') then
    Result := 'Amulet'
  else if HasKeyword(e, 'ClothingRing') then
    Result := 'Ring'
  else if HasKeyword(e, 'WeapTypeDagger') then
    Result := 'Dagger'
  else if HasKeyword(e, 'WeapTypeSword') then
    Result := 'Sword'
  else if HasKeyword(e, 'WeapTypeWarAxe') then
    Result := 'WarAxe'
  else if HasKeyword(e, 'WeapTypeMace') then
    Result := 'Mace'
  else if HasKeyword(e, 'WeapTypeGreatsword') then
    Result := 'Greatsword'
  else if HasKeyword(e, 'WeapTypeBattleaxe') then
    Result := 'Battleaxe'
  else if HasKeyword(e, 'WeapTypeWarhammer') then
    Result := 'Warhammer'
  else if HasKeyword(e, 'WeapTypeBow') then
    Result := 'Bow'
  else if HasKeyword(e, 'WeapTypeStaff') then
    Result := 'Staff'
  else
    Result := nil;
end;

function GetItemDescription(e: IInterface): String;
var
  ench: IInterface;
begin
  ench := WinningOverride(LinksTo(ElementByPath(e, 'EITM - Object Effect')));
  if not Assigned(ench) then begin
    Result := GetElementNativeValues(e, 'DESC - Description');
    Exit;
  end;

  re_number.Subject := GetElementNativeValues(e, 'DESC - Description');
  if re_number.Match then begin
    Result := GetElementNativeValues(e, 'DESC - Description');
    Exit;
  end;
  
  Result := GetEffectsDescription(ench);
  if not Assigned(Result) then
    Result := GetElementNativeValues(e, 'DESC - Description');
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
    re_ench_apply.Subject := EditorID(magicEffect);
    re_ench_equip_ability.Subject := EditorID(magicEffect);
    if re_ench_apply.Match then
      description := GetApplyDescription(magicEffect, re_ench_apply.Groups[1])
    else if re_ench_equip_ability.Match then
      description := GetEquipAbilityDescription(magicEffect)
    else if GetElementNativeValues(magicEffect, 'Magic Effect Data\DATA - Data\Flags\Hide in UI') = 0 then begin
      description := GetElementNativeValues(magicEffect, 'DNAM - Magic Item Description');
      mag := GetElementNativeValues(effect, 'EFIT - EFIT\Magnitude');
      dur := GetElementNativeValues(effect, 'EFIT - EFIT\Duration');
      description := StringReplace(description, '<mag>', Format('<%d>', [mag]), rfReplaceAll);
      description := StringReplace(description, '<dur>', Format('<%d>', [dur]), rfReplaceAll);
    end
    else
      Continue;
    if Assigned(description) then
        Result := Result + ';' + description
  end;
  if Assigned(Result) then
    Delete(Result, 1, 1);
end;

function GetApplyDescription(e: IInterface; s: String): String;
var
  scripts, script, properties, prop, arr, spell: IInterface;
  description: String;
  i, j, k: Integer;
begin
  Result := nil;
  scripts := ElementByPath(e, 'VMAD - Virtual Machine Adapter\Scripts');
  if not Assigned(scripts) then
    Exit;
  for i := 0 to Pred(ElementCount(scripts)) do begin
    script := ElementByIndex(scripts, i);
    properties := ElementByPath(script, 'Properties');
    if not Assigned(properties) then
      Exit;
    for j := 0 to Pred(ElementCount(properties)) do begin
      prop := ElementByIndex(properties, j);
      if GetElementEditValues(prop, 'Type') = 'Object' then begin
        spell := WinningOverride(LinksTo(ElementByPath(prop, 'Value\Object Union\Object v2\FormID')));
        if (Signature(spell) <> 'SPEL') or (Pos(s, EditorID(spell)) <> 1) then
          Continue;
        description := GetEffectsDescription(spell);
        Result := Result + ';' + description;
      end
      else if GetElementEditValues(prop, 'Type') = 'Array of Object' then begin
        arr := ElementByPath(prop, 'Value\Array of Object');
        for k := 0 to Pred(ElementCount(arr)) do begin
          spell := WinningOverride(LinksTo(ElementByPath(ElementByIndex(arr, k), 'Object v2\FormID')));
          if (Signature(spell) <> 'SPEL') or (Pos(s, EditorID(spell)) <> 1) then
            Continue;
          description := GetEffectsDescription(spell);
          Result := Result + ';' + description;
        end;
      end;
    end;
  end;
  if Assigned(Result) then
    Delete(Result, 1, 1);
end;

function GetEquipAbilityDescription(e: IInterface): String;
var
  equipAbility: IInterface;
begin
  equipAbility := WinningOverride(LinksTo(ElementByPath(e, 'Magic Effect Data\DATA - Data\Equip Ability')));
  Result := GetEffectsDescription(equipAbility);
end;

end.
