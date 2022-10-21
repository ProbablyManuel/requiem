unit Name1stPersonWeapon;

var
  re_weapon, re_weapon_artifact: TPerlRegEx;


function Initialize: Integer;
begin
  re_weapon := TPerlRegEx.Create;
  re_weapon.RegEx := '^[^_]+_Weapon_([^_]+)_([^_]+)$';

  re_weapon_artifact := TPerlRegEx.Create;
  re_weapon_artifact.RegEx := '^[^_]+_(Artifact_.+)$';
end;

function Process(e: IInterface): integer;
var
  i: Integer;
  weapon: IInterface;
begin
  if Signature(e) <> 'STAT' then Exit;

  for i := 0 to Pred(ReferencedByCount(e)) do begin
    weapon := WinningOverride(ReferencedByIndex(e, i));
    if Signature(weapon) = 'WEAP' then begin
      re_weapon.Subject := EditorID(weapon);
      re_weapon_artifact.Subject := EditorID(weapon);
      if re_weapon.Match or re_weapon_artifact.Match then
        SetEditorID(e, StringReplace(EditorID(weapon), '_', '_1stPerson_', nil))
    end;
  end;
end;

function Finalize: Integer;
begin
  re_weapon.Free;
  re_weapon_artifact.Free;
end;

end.
