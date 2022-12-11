unit AddItemsToOutfit;

uses REQ_Util;

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

end.
