unit ExportFormIdPairs;

var
  formIdPairs, signatures: TStringList;


function Initialize: Integer;
begin
  formIdPairs := TStringList.Create;
  formIdPairs.Sorted := True;
  formIdPairs.Duplicates := dupIgnore;

  signatures := TStringList.Create;
  signatures.Add('ARMO');
  signatures.Add('INGR');
  signatures.Add('MISC');
  signatures.Add('PERK');
  signatures.Add('SLGM');
  signatures.Add('WEAP');
end;

function Process(e: IInterface): integer;
var
  fixedFormId: Integer;
  plugin, s: String;
begin
  if signatures.IndexOf(Signature(e)) = -1 then Exit;
  fixedFormId := GetLoadOrderFormID(e) mod $1000000;
  plugin := GetFileName(GetFile(MasterOrSelf(e)));
  formIdPairs.Add(Format('%s=%s:%.6x', [EditorID(e), plugin, fixedFormId]));
end;

function Finalize: Integer;
var
  dlgSave: TSaveDialog;
begin
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
