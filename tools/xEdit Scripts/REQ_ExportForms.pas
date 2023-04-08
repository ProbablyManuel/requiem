unit ExportForms;

var
  byEditorID, byFullName, signatures: TStringList;


function Initialize: Integer;
begin
  byEditorID := TStringList.Create;
  byEditorID.Sorted := True;
  byEditorID.Duplicates := dupIgnore;

  byFullName := TStringList.Create;
  byFullName.Sorted := True;
  byFullName.Duplicates := dupIgnore;

  signatures := TStringList.Create;
  signatures.Add('ALCH');
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
  plugin, fullName: String;
begin
  if signatures.IndexOf(Signature(e)) = -1 then Exit;
  fixedFormId := GetLoadOrderFormID(e) mod $1000000;
  plugin := GetFileName(GetFile(MasterOrSelf(e)));
  byEditorID.Add(Format('%s,%s:%.6x', [EditorID(e), plugin, fixedFormId]));
  fullName := GetElementNativeValues(e, 'FULL - Name');
  if fullName <> '' then
    if pos(',', fullName) <> 0 then
      byFullName.Add(Format('"%s",%s:%.6x', [fullName, plugin, fixedFormId]))
    else
      byFullName.Add(Format('%s,%s:%.6x', [fullName, plugin, fixedFormId]));
end;

function Finalize: Integer;
var
  targetDir: String;
begin
  targetDir := DataPath + 'tools\xEdit Scripts\patcher_data\';
  byEditorID.SaveToFile(targetDir + 'FormsByEditorID.csv');
  byFullName.SaveToFile(targetDir + 'FormsByFullName.csv');
  byEditorID.Free;
  byFullName.Free;
end;

end.
