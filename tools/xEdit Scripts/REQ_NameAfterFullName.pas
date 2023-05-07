unit NameAfterFullName;

uses REQ_Util;

var
  prefix: String;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'Prefix', prefix) then
    Result := -1;
end;

function Process(e: IInterface): Integer;
var
  newEditorID: String;
begin
  newEditorID := prefix + StrToEditorID(GetElementNativeValues(e, 'FULL - Name'));
  if newEditorID <> EditorID(e) then
    SetEditorID(e, newEditorID);
end;

function Finalize: Integer;
begin

end;

end.
