unit AddNpcsToLeveledList;

uses REQ_Util;

var
  target: String;
  leveledNpc: IInterface;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'EditorID or $-prefixed FormID', target) then
    Result := -1;
end;

function Process(e: IInterface): Integer;
var
  entryArray, entry: IInterface;
begin
  if not Assigned(leveledNpc) then begin
    if Pos('$', target) = 1 then
      leveledNpc := WinningOverride(RecordByLoadOrderFormID(StrToInt(target)))
    else begin
      leveledNpc := AddRecordToFile(GetFile(e), 'LVLN');
      SetEditorID(leveledNpc, target);
    end;
    entryArray := ElementByPath(leveledNpc, 'Leveled List Entries');
    if not Assigned(entryArray) then begin
      entryArray := Add(leveledNpc, 'Leveled List Entries', True);
      entry := ElementByIndex(entryArray, 0);
    end
    else
      entry := ElementAssign(entryArray, HighInteger, nil, False);
  end
  else begin
    entryArray := ElementByPath(leveledNpc, 'Leveled List Entries');
    entry := ElementAssign(entryArray, HighInteger, nil, False);
  end;
  SetElementEditValues(entry, 'LVLO - Base Data\Level', '1');
  SetElementEditValues(entry, 'LVLO - Base Data\Reference', IntToHex(GetLoadOrderFormID(e), 8));
  SetElementEditValues(entry, 'LVLO - Base Data\Count', '1');
end;

function Finalize: Integer;
begin
end;

end.
