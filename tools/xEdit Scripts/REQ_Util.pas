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

function RecordByLoadOrderFormID(aiFormID: Cardinal): IwbMainRecord;
var
  loadOrderIndex: Cardinal;
  i: Integer;
  f: IwbFile;
begin
  loadOrderIndex := 0;
  for i := 0 to Pred(FileCount) do begin
    f := FileByIndex(i);
    if IsHeavyPlugin(f) then begin
      if loadOrderIndex = aiFormID shr 24 then
        Break;
      loadOrderIndex := loadOrderIndex + 1;
    end;
  end;
  Result := RecordByFormID(f, LoadOrderFormIDToFileFormID(f, aiFormID), False);
end;

function IsHeavyPlugin(aeFile: IwbFile): Boolean;
var
  fileExt: String;
begin
  fileExt := ExtractFileExt(GetFileName(aeFile));
  Result := (GetElementNativeValues(ElementByIndex(aeFile, 0), 'Record Header\Record Flags\ESL') = 0) and (SameText(fileExt, '.esp') or SameText(fileExt, '.esm'));
end;

function IsLightPlugin(aeFile: IwbFile): Boolean;
var
  fileExt: String;
begin
  fileExt := ExtractFileExt(GetFileName(aeFile));
  Result := (GetElementNativeValues(ElementByIndex(f, 0), 'Record Header\Record Flags\ESL') = 1) or SameText(fileExt, '.esl');
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

function CreateOrGetFile(asFile: String): IwbFile;
var
  i: Integer;
begin
  for i := 0 to Pred(FileCount) do begin
    if GetFileName(FileByIndex(i)) = asFile then begin
      Result := FileByIndex(i);
      Exit;
    end;
  end;
  Result := AddNewFileName(asFile);
end;

procedure AddAllMastersToFile(aeFile: IwbFile);
var
  i: Integer;
begin
  for i := 0 to Pred(FileCount) do begin
    if Equals(aeFile, FileByIndex(i)) then
      Exit;
    if i <> 1 then
      AddMasterIfMissing(aeFile, GetFileName(FileByIndex(i)));
  end;
end;

function PairToLoadOrderFormID(asFormIDPair: String): Cardinal;
var
  formIDPair: TStringList;
  e, f: IInterface;
  nativeFormID: Cardinal;
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

function GetElementLoadOrderFormID(aeContainer: IwbContainer; asPath: String): Cardinal;
begin
  Result := FileFormIDtoLoadOrderFormID(GetFile(aeContainer), GetElementNativeValues(aeContainer, asPath))
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

function StrToEditorID(s: String): String;
var
  i: Integer;
  word: String;
  words: TStringList;
  re: TPerlRegEx;
begin
  re := TPerlRegEx.Create;
  re.RegEx := '[^a-zA-Z0-9_ ]';
  re.Subject := s;
  re.Replacement := '';
  re.ReplaceAll;
  words := TStringList.Create;
  words.DelimitedText := re.Subject;
  Result := '';
  for i := 0 to Pred(words.Count) do begin
    word := words[i];
    word := UpperCase(word[1]) + Copy(word, 2, Length(word));
    Result := Result + word;
  end;
  words.Free;
  re.Free;
end;

function KeywordToLoadOrderFormID(asKeyword: String): Cardinal;
var
  i, j: Integer;
  e, f, g: IInterface;
begin
  for i := 0 to Pred(FileCount) do begin
    f := FileByIndex(i);
    g := GroupBySignature(f, 'KYWD');
    for j := 0 to Pred(ElementCount(g)) do begin
      e := ElementByIndex(g, j);
      if EditorID(e) = asKeyword then begin
        Result := GetLoadOrderFormID(e);
        Exit;
      end;
    end;
  end;
  Result := 0;
end;

function HasKeyword(aeElement: IwbElement; asKeyword: String): Boolean;
var
  kwda: IwbElement;
  i: Integer;
begin
  kwda := ElementByPath(aeElement, 'KWDA');
  for i := 0 to Pred(ElementCount(kwda)) do
    if EditorID(LinksTo(ElementByIndex(kwda, i))) = asKeyword then begin
      Result := True;
      Exit;
    end;
  Result := False;
end;

procedure AddKeyword(aeElement: IwbElement; asKeyword: String);
var
  keyword: Cardinal;
  kwda: IwbElement;
  i: Integer;
begin
  keyword := KeywordToLoadOrderFormID(asKeyword);
  kwda := ElementByPath(aeElement, 'KWDA');
  if Assigned(kwda) then begin
    for i := 0 to Pred(ElementCount(kwda)) do
      if GetLoadOrderFormID(LinksTo(ElementByIndex(kwda, i))) = keyword then
        Exit;
    SetEditValue(ElementAssign(kwda, HighInteger, nil, False), IntToHex(keyword, 8));
  end
  else begin
    kwda := Add(aeElement, 'KWDA', True);
    SetEditValue(ElementByIndex(kwda, 0), IntToHex(keyword, 8));
  end;
end;

procedure RemoveKeyword(aeElement: IwbElement; asKeyword: String);
var
  keyword: Cardinal;
  i: Integer;
  kwda: IwbElement;
begin
  keyword := KeywordToLoadOrderFormID(asKeyword);
  kwda := ElementByPath(aeElement, 'KWDA');
  if ElementCount(kwda) = 1 then begin
    if GetLoadOrderFormID(LinksTo(ElementByIndex(kwda, 0))) = keyword then
      RemoveElement(aeElement, kwda);
  end
  else
    for i := 0 to Pred(ElementCount(kwda)) do
      if GetLoadOrderFormID(LinksTo(ElementByIndex(kwda, i))) = keyword then
        if not Assigned(RemoveByIndex(kwda, i, True)) then
          AddMessage('Failed to remove ' + asKeyword + ' from ' + Name(aeElement));
end;

function BoolToInt(abValue: Boolean): Integer;
begin
  if abValue then
    Result := 1
  else
    Result := 0;
end;

end.
