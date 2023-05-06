unit NameMagicEffect;

uses REQ_Util;

var
  re_cloak_spell, re_hazard_spell, re_ignore, re_spell: TPerlRegEx;


function Initialize: Integer;
begin
  re_cloak_spell := TPerlRegEx.Create;
  re_cloak_spell.RegEx := '^([^_]+)_Cloak_([^_]+)$';

  re_hazard_spell := TPerlRegEx.Create;
  re_hazard_spell.RegEx := '^([^_]+)_HazardSpell_([^_]+)$';

  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^([^_]+)_(DEPRECATED|LEGACY|NULL)_(.+)';

  re_spell := TPerlRegEx.Create;
  re_spell.RegEx := '^([^_]+)_(Alteration|Conjuration|Destruction|Illusion|Restoration)[0-5]?_([^_]+)$';
end;


function Process(e: IInterface): Integer;
var
  i: Integer;
  r: IInterface;
  skill, newEditorID: String;
begin
  if Signature(e) <> 'MGEF' then
    Exit;

  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then
    Exit;

  skill := GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Magic Skill');
  if (skill <> 'Alteration') and (skill <> 'Conjuration') and (skill <> 'Destruction') and (skill <> 'Illusion') and (skill <> 'Restoration') then
    Exit;

  newEditorID := nil;
  for i := 0 to Pred(ReferencedByCount(e)) do begin
    r := ReferencedByIndex(e, i);
    if (Signature(r) = 'SPEL') and IsWinningOverride(r) then begin
      re_spell.Subject := EditorID(r);
      if re_spell.Match then
        if Assigned(newEditorID) then begin
          AddMessage('Multiple spells found for ' + Name(e));
          Exit;
        end
        else
          newEditorID := re_spell.Groups[0] + '_' + StrToEditorID(GetElementNativeValues(e, 'FULL - Name'));

      re_cloak_spell.Subject := EditorID(r);
      if re_cloak_spell.Match then
        if Assigned(newEditorID) then begin
          AddMessage('Multiple spells found for ' + Name(e));
          Exit;
        end
        else
          newEditorID := re_cloak_spell.Groups[1] + '_' + skill + '_' + re_cloak_spell.Groups[2] + '_' + StrToEditorID(GetElementNativeValues(e, 'FULL - Name'));

      re_hazard_spell.Subject := EditorID(r);
      if re_hazard_spell.Match then
        if Assigned(newEditorID) then begin
          AddMessage('Multiple spells found for ' + Name(e));
          Exit;
        end
        else
          newEditorID := re_hazard_spell.Groups[1] + '_' + skill + '_' + re_hazard_spell.Groups[2] + '_' + StrToEditorID(GetElementNativeValues(e, 'FULL - Name'));
    end;
  end;
  if not Assigned(newEditorID) then
    AddMessage('No Spell found for ' + Name(e))
  else
    SetEditorID(e, newEditorID);
end;


function Finalize: Integer;
begin
  re_cloak_spell.Free;
  re_hazard_spell.Free;
  re_ignore.Free;
  re_spell.Free;
end;

end.
