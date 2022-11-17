unit AddItemsToOutfit;

var
  newEditorID: String;
  outfit: IInterface;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'EditorID', newEditorID) then
    Result := -1;
  outfit := nil;
end;


function Process(e: IInterface): Integer;
var
  entryArray, entry: IInterface;
begin
  if not Assigned(outfit) then begin
    outfit := AddRecordToFile(GetFile(e), 'OTFT');
    SetEditorID(outfit, newEditorID);
    entryArray := Add(outfit, 'INAM', True);
  end
  else
    entryArray := ElementByPath(outfit, 'INAM');
  entry := ElementAssign(entryArray, HighInteger, nil, False);
  SetEditValue(entry, IntToHex(GetLoadOrderFormID(e), 8));
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
