unit AddItemsToLeveledList;

var
  newEditorID: String;
  leveledItem: IInterface;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'EditorID', newEditorID) then
    Result := -1;
  leveledItem := nil;
end;


function Process(e: IInterface): Integer;
var
  entryArray, entry: IInterface;
begin
  if not Assigned(leveledItem) then begin
    leveledItem := AddRecordToFile(GetFile(e), 'LVLI');
    SetEditorID(leveledItem, newEditorID);
    entryArray := Add(leveledItem, 'Leveled List Entries', True);
    entry := ElementByIndex(entryArray, 0);
  end
  else begin
    entryArray := ElementByPath(leveledItem, 'Leveled List Entries');
    entry := ElementAssign(entryArray, HighInteger, nil, False);
  end;
  SetElementNativeValues(entry, 'LVLO - Base Data\Level', 1);
  SetElementNativeValues(entry, 'LVLO - Base Data\Reference', LoadOrderFormIDtoFileFormID(GetFile(e), GetLoadOrderFormID(e)));
  SetElementNativeValues(entry, 'LVLO - Base Data\Count', 1);
end;


function Finalize: Integer;
begin

end;

function AddRecordToFile(aeFile: IwbFile; asSignature: String): IInterface;
var
  group: IInterface;
begin
  if HasGroup(aeFile, asSignature) then
    group := GroupBySignature(aeFile, asSignature)
  else
    group := Add(aeFile, asSignature, True);
  Result := Add(group, asSignature, True);
end;

end.
