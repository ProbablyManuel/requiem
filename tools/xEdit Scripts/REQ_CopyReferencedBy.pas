unit CopyReferencedBy;

uses REQ_Util;

var
  plugin: IInterface;


function Initialize: Integer;
var
  pluginName: String;
begin
  InputQuery('Enter', 'Filename without extension', pluginName);
  pluginName := pluginName + '.esp';
  plugin := CreateOrGetFile(pluginName);
  AddAllMastersToFile(plugin);
end;

function Process(e: IInterface): Integer;
var
  i: Integer;
  ref: IInterface;
  referencedBy: TList;
begin
  referencedBy := TList.Create;
  referencedBy.Capacity := ReferencedByCount(e);
  for i := 0 to Pred(ReferencedByCount(e)) do
    referencedBy.Add(ReferencedByIndex(e, i));
  for i := 0 to Pred(referencedBy.Count()) do begin
    ref := ObjectToElement(referencedBy.Items[i]);
    if IsWinningOverride(ref) and not Equals(GetFile(ref), plugin) then
      wbCopyElementToFile(ref, plugin, False, True);
  end;
  referencedBy.Free;
end;

function Finalize: Integer;
begin
  CleanMasters(plugin);
end;

end.
