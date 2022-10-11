unit NameAfterFullName;

var
  prefix: String;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'Prefix', prefix) then
    Result := -1;
end;

function Process(e: IInterface): Integer;
var
  i: Integer;
  newEditorID, word: String;
  words: TStringList;
begin
  words := TStringList.Create;
  words.DelimitedText := GetElementNativeValues(e, 'FULL - Name');
  newEditorID := prefix;
  for i := 0 to Pred(words.Count) do begin
    word := words[i];
    word := StringReplace(word, '''', '', [rfReplaceAll]);
    word := UpperCase(word[1]) + Copy(word, 2, Length(word));
    newEditorID := newEditorID + word;
  end;
  if newEditorID <> EditorID(e) then
    SetEditorID(e, newEditorID);
end;

function Finalize: Integer;
begin

end;

end.