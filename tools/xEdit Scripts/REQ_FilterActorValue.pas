unit FilterActorValue;

var
  av: String;


function Filter(e: IInterface): Boolean;
var
  effects, effect: IInterface;
  i: Integer;
begin
  Result := False;
  // if Signature(e) = 'BPTD' then begin
  //   'Body Parts\Body Part\BPND - BPND\Actor Value'
  if Signature(e) = 'MGEF' then begin
    Result := Result or SameText(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Actor Value'), av);
    Result := Result or SameText(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Second Actor Value'), av);
    Result := Result or SameText(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Magic Skill'), av);
    Result := Result or SameText(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Resist Value'), av);
  end
  else if Signature(e) = 'PERK' then begin
    effects := ElementByPath(e, 'Effects');
    for i := 0 to Pred(ElementCount(effects)) do begin
      effect := ElementByIndex(effects, i);
      Result := Result or SameText(GetElementEditValues(effect, 'Function Parameters\EPFD - Data\Actor Value, Float\Actor Value'), av);
    end;
  end
  // if Signature(e) = 'RACE' then begin
  //   'RACE\DATA - DATA\Skill Boosts\Skill Boost\Skill'
  else if Signature(e) = 'WEAP' then
    Result := SameText(GetElementEditValues(e, 'DNAM - Data\Resist'), av);

  if not Result then
    Result := EvaluateCondition(e);
end;

function EvaluateCondition(e: IInterface): Boolean;
var
  i: Integer;
  c: IInterface;
  parameter: String;
begin
  if SameText(Name(e), 'Conditions') then
    for i := 0 to Pred(ElementCount(e)) do begin
      c := ElementByIndex(e, i);
      if SameText(GetElementEditValues(c, 'CTDA - CTDA\Parameter #1'), av) then begin
        Result := True;
        Exit;
      end;
      if SameText(GetElementEditValues(c, 'CTDA - CTDA\Parameter #2'), av) then begin
        Result := True;
        Exit;
      end;
    end
  else
    for i := 0 to Pred(ElementCount(e)) do
      if EvaluateCondition(ElementByIndex(e, i)) then begin
        Result := True;
        Exit;
      end;
  Result := False;
end;

function Initialize: Integer;
begin
  if not InputQuery('Enter', 'Actorvalue', av) then
    Result := -1;

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
  FilterBySignature := False;
  FilterSignatures := '';
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