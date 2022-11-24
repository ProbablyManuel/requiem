unit SaveFormIdPair;

var
  formIdPairs: TStringList;


function Initialize: Integer;
begin
  formIdPairs := TStringList.Create;
end;

function Process(e: IInterface): integer;
var
  identifier, fixedFormId, plugin: String;
begin
  // identifier := GetElementNativeValues(e, 'FULL');
  identifier := EditorID(e);
  fixedFormId := IntToHex(GetLoadOrderFormID(e) mod $1000000, 6);
  plugin := GetFileName(GetFile(MasterOrSelf(e)));
  formIdPairs.Add(Format('%s=%s:%s', [identifier, fixedFormId, plugin]));
end;

function Finalize: Integer;
var
  dlgSave: TSaveDialog;
begin
  formIdPairs.Sort;
  if formIdPairs.Count > 0 then begin
    dlgSave := TSaveDialog.Create(nil);
    dlgSave.Options := dlgSave.Options + [ofOverwritePrompt];
    dlgSave.InitialDir := DataPath;
    dlgSave.FileName := 'FormIdPairs';
    dlgSave.Filter := 'Text files (*.txt)|*.txt|All files (*.*)|*.*';
    dlgSave.DefaultExt := 'txt';
    if dlgSave.Execute then
      formIdPairs.SaveToFile(dlgSave.FileName);
    dlgSave.Free;
  end;
  formIdPairs.Free;
end;

end.
