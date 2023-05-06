unit REQ_Util;


function LookupFormByEditorID(asSignatures: String): THashedStringList;
var
  i, j, k, indexOfEditorID: Integer;
  e, f, g: IInterface;
  hexFormID: String;
  signatures, table: TStringList;
  hashTable: THashedStringList;
begin
  signatures := TStringList.Create;
  signatures.DelimitedText := asSignatures;
  table := TStringList.Create;
  for i := 0 to Pred(FileCount) do begin
    f := FileByIndex(i);
    for j := 0 to Pred(signatures.Count) do begin
      g := GroupBySignature(f, signatures[j]);
      for k := 0 to Pred(ElementCount(g)) do begin
        e := ElementByIndex(g, k);
        hexFormID := IntToHex(GetLoadOrderFormID(e), 8);
        indexOfEditorID := table.indexOfName(EditorID(e));
        if not ElementExists(e, 'EDID - Editor ID') then
          AddMessage('Missing EditorID: ' + Name(e))
        else if indexOfEditorID = -1 then
          table.Add(EditorID(e) + '=' + hexFormID)
        else if table.ValueFromIndex[indexOfEditorID] <> hexFormID then
          AddMessage('Duplicate EditorID: ' + Name(e));
      end;
    end;
  end;
  hashTable := THashedStringList.Create;
  hashTable.Assign(table);
  Result := hashTable;
  signatures.Free;
  table.Free;
end;

function FileByName(asFile: string): IwbFile;
var
  i: integer;
begin
  for i := 0 to Pred(FileCount) do begin
    if GetFileName(FileByIndex(i)) = asFile then begin
      Result := FileByIndex(i);
      Exit;
    end;
  end;
  Result := nil;
end;

function PairToLoadOrderFormID(asFormIDPair: String): Integer;
var
  formIDPair: TStringList;
  e, f: IInterface;
  nativeFormID: Integer;
begin
  formIDPair := TStringList.Create;
  formIDPair.Delimiter := ':';
  formIDPair.StrictDelimiter := True;
  formIDPair.DelimitedText := asFormIDPair;
  f := FileByName(formIDPair[0]);
  nativeFormID := MasterCount(f) shl 24 + StrToInt('$' + formIDPair[1]);
  e := RecordByFormID(f, nativeFormID, True);
  Result := GetLoadOrderFormID(e);
  formIDPair.Free;
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

function GetSingleReferencedBy(e: IInterface; s: String): IInterface;
var
  r: IInterface;
  i: Integer;
begin
  Result := nil;
  for i := 0 to Pred(ReferencedByCount(e)) do begin
    r := ReferencedByIndex(e, i);
    if (Signature(r) <> s) or not IsWinningOverride(r) then
      Continue;
    if Assigned(Result) then begin
      Result := nil;
      Exit;
    end
    else
      Result := r;
  end;
end;

function GetSingleReferencedByRegex(e: IInterface; s: String; re: TPerlRegEx): IInterface;
var
  r: IInterface;
  i: Integer;
begin
  Result := nil;
  for i := 0 to Pred(ReferencedByCount(e)) do begin
    r := ReferencedByIndex(e, i);
    if (Signature(r) <> s) or not IsWinningOverride(r) then
      Continue;
    re.Subject := EditorID(r);
    if not re.Match then
      Continue;
    if Assigned(Result) then begin
      Result := nil;
      Exit;
    end
    else
      Result := r;
  end;
end;

end.
