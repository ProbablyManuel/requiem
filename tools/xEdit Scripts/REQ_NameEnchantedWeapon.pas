unit NameEnchantedWeapon;

var
  slWeapons: TStringList;
  re_weapon, re_enchantment: TPerlRegEx;


function Initialize: Integer;
begin
  slWeapons := TStringList.Create;

  re_weapon := TPerlRegEx.Create;
  re_weapon.RegEx := '^[^_]+_([^_]+)_([^_]+)$';

  re_enchantment := TPerlRegEx.Create;
  re_enchantment.RegEx := '^REQ_Ench_Weapon_(.+)$';

end;

function Process(e: IInterface): Integer;
var
  enchantment, template: IInterface;
  enchantmentName, weaponSet, weaponType, weaponEditorID: String;
begin
  if Signature(e) <> 'WEAP' then
    Exit;

  enchantment := WinningOverride(LinksTo(ElementByPath(e, 'EITM - Object Effect')));
  if not Assigned(enchantment) then begin
    AddMessage(Name(e) + ' has no enchantment');
    Exit;
  end;
  re_enchantment.Subject := EditorID(enchantment);
  if not re_enchantment.Match then begin
    AddMessage(Name(e) + ' has unrecognized enchantment');
    Exit;
  end;
  enchantmentName := re_enchantment.Groups[1];

  template := WinningOverride(LinksTo(ElementByPath(e, 'CNAM - Template')));
  if not Assigned(template) then begin
    AddMessage(Name(e) + ' has no template');
    Exit;
  end;
  re_weapon.Subject := EditorID(template);
  if not re_weapon.Match then begin
    AddMessage(Name(e) + ' has unrecognized template');
    Exit;
  end;
  weaponSet := re_weapon.Groups[1];
  weaponType := re_weapon.Groups[2];

  weaponEditorID := Format('REQ_Ench_%s_%s_%s', [weaponSet, weaponType, enchantmentName]);
  if EditorID(e) <> weaponEditorID then
    SetEditorID(e, weaponEditorID);
  slWeapons.Add(weaponEditorID);
end;

function Finalize: Integer;
var
  dlgSave: TSaveDialog;
begin
  slWeapons.Sort;
  if slWeapons.Count > 0 then begin
    dlgSave := TSaveDialog.Create(nil);
    dlgSave.Options := dlgSave.Options + [ofOverwritePrompt];
    dlgSave.InitialDir := DataPath;
    dlgSave.FileName := 'WeaponEnchantments';
    dlgSave.Filter := 'Text files (*.txt)|*.txt|All files (*.*)|*.*';
    dlgSave.DefaultExt := 'txt';
    if dlgSave.Execute then
      slWeapons.SaveToFile(dlgSave.FileName);
    dlgSave.Free;
  end;
  slWeapons.Free;
end;

end.