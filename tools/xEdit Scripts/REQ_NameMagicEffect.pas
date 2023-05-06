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
  source_spell, newEditorID: String;
begin
  if Signature(e) <> 'MGEF' then
    Exit;

  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then
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
          newEditorID := re_spell.Groups[0] + '_' + FullNameToEditorID(e);

      re_cloak_spell.Subject := EditorID(r);
      if re_cloak_spell.Match then
        if Assigned(newEditorID) then begin
          AddMessage('Multiple spells found for ' + Name(e));
          Exit;
        end
        else begin
            source_spell := GetEditorIdForCloakSpell(r);
            if Assigned(source_spell) then
              newEditorID := source_spell + '_' + FullNameToEditorID(e);
          end;

      re_hazard_spell.Subject := EditorID(r);
      if re_hazard_spell.Match then
        if Assigned(newEditorID) then begin
          AddMessage('Multiple spells found for ' + Name(e));
          Exit;
        end
        else begin
            source_spell := GetEditorIdForHazardSpell(r);
            if Assigned(source_spell) then
              newEditorID := source_spell + '_' + FullNameToEditorID(e);
          end;
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


function GetEditorIdForCloakSpell(e: IInterface): String;
var
  effect, spell: IInterface;
begin
  Result := nil;

  effect := GetSingleReferencedBy(e, 'MGEF');
  if not Assigned(effect) then begin
    AddMessage('Many or zero MGEF found for ' + Name(e));
    Exit;
  end;

  spell := GetSingleReferencedBy(effect, 'SPEL');
  if not Assigned(spell) then begin
    AddMessage('Many or zero SPEL found for ' + Name(e));
    Exit;
  end;

  re_spell.Subject := EditorID(spell);
  if not re_spell.Match then
    AddMessage('Not a valid spell: ' + re_spell.Groups[0])
  else
    Result := re_spell.Groups[1] + '_' + re_spell.Groups[2] + '_' + re_spell.Groups[3];
end;


function GetEditorIdForHazardSpell(e: IInterface): String;
var
  hazard, impact, impact_set, effect, spell: IInterface;
begin
  Result := nil;

  hazard := GetSingleReferencedBy(e, 'HAZD');
  if not Assigned(hazard) then begin
    AddMessage('Many or zero HAZD found for ' + Name(e));
    Exit;
  end;

  impact := GetSingleReferencedBy(hazard, 'IPCT');
  if not Assigned(impact) then begin
    AddMessage('Many or zero IPCT found for ' + Name(e));
    Exit;
  end;

  impact_set := GetSingleReferencedBy(impact, 'IPDS');
  if not Assigned(impact_set) then begin
    AddMessage('Many or zero PIDS found for ' + Name(e));
    Exit;
  end;

  effect := GetSingleReferencedBy(impact_set, 'MGEF');
  if not Assigned(effect) then begin
    AddMessage('Many or zero MGEF found for ' + Name(e));
    Exit;
  end;

  spell := GetSingleReferencedBy(effect, 'SPEL');
  if not Assigned(spell) then begin
    AddMessage('Many or zero SPEL found for ' + Name(e));
    Exit;
  end;

  re_spell.Subject := EditorID(spell);
  if not re_spell.Match then
    AddMessage('Not a valid spell: ' + re_spell.Groups[0])
  else
    Result := re_spell.Groups[1] + '_' + re_spell.Groups[2] + '_' + re_spell.Groups[3];
end;


function FullNameToEditorID(e: IInterface): String;
begin
  Result := GetElementNativeValues(e, 'FULL - Name');
  Result := StringReplace(Result, '(Rank I)', '', nil);
  Result := StringReplace(Result, '(Rank II)', '', nil);
  Result := StringReplace(Result, '(Rank III)', '', nil);
  Result := StringReplace(Result, '(Rank IV)', '', nil);
  Result := StringReplace(Result, '(Rank V)', '', nil);
  Result := StringReplace(Result, ' to ', ' To ', nil);
  Result := StringReplace(Result, ' of ', ' Of ', nil);
  Result := StringReplace(Result, ' the ', ' The ', nil);
  Result := StringReplace(Result, ' and ', ' And ', nil);
  Result := StringReplace(Result, ' ', '', [rfReplaceAll]);
end;

end.
