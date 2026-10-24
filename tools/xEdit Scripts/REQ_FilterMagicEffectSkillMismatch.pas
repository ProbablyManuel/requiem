unit FilterMagicEffectSkillMismatch;


function Filter(e: IInterface): Boolean;
var
  skill, school: String;
begin
  Result := False;
  if not IsWinningOverride(e) then
    Exit;
  if SameText(Copy(EditorID(e), 1, 9), 'REQ_NULL_') then
    Exit;
  skill := GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Magic Skill');
  school := SchoolFromEditorID(EditorID(e));
  if school <> '' then
    Result := not SameText(skill, school)
  else
    Result := not SameText(skill, 'None');
end;

function SchoolFromEditorID(edid: String): String;
var
  schools: TStringList;
  i: Integer;
begin
  Result := '';
  schools := TStringList.Create;
  schools.CommaText := 'Alteration,Conjuration,Destruction,Illusion,Restoration';
  for i := 0 to Pred(schools.Count) do
    if SameText(Copy(edid, 1, Length(schools[i]) + 4), 'REQ_' + schools[i]) then begin
      Result := schools[i];
      Break;
    end;
  schools.Free;
end;


function Initialize: Integer;
begin
  FilterConflictAll := False;
  FilterConflictThis := False;
  FilterByInjectStatus := False;
  FilterInjectStatus := False;
  FilterByNotReachableStatus := False;
  FilterNotReachableStatus := False;
  FilterByReferencesInjectedStatus := False;
  FilterReferencesInjectedStatus := False;
  FilterByEditorID := False;
  FilterEditorID := '';
  FilterByName := False;
  FilterName := '';
  FilterByBaseEditorID := False;
  FilterBaseEditorID := '';
  FilterByBaseName := False;
  FilterBaseName := '';
  FilterScaledActors := False;
  FilterByPersistent := False;
  FilterPersistent := False;
  FilterUnnecessaryPersistent := False;
  FilterMasterIsTemporary := False;
  FilterIsMaster := False;
  FilterPersistentPosChanged := False;
  FilterDeleted := False;
  FilterByVWD := False;
  FilterVWD := False;
  FilterByHasVWDMesh := False;
  FilterHasVWDMesh := False;
  FilterBySignature := True;
  FilterSignatures := 'MGEF';
  FilterByBaseSignature := False;
  FilterBaseSignatures := '';
  FlattenBlocks := False;
  FlattenCellChilds := False;
  AssignPersWrldChild := False;
  InheritConflictByParent := False;
  FilterScripted := True; // use custom Filter() function

  ApplyFilter;
end;

end.
