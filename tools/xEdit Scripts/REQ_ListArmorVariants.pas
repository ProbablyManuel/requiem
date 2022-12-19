unit ListArmorVariants;

var
  armors: TStringList;
  re_armor: TPerlRegEx;


function Initialize: Integer;
begin
  armors := TStringList.Create;

  re_armor := TPerlRegEx.Create;
  re_armor.RegEx := '^[^_]+_(Heavy|Light)_([^_]+)_([^_]+)_([^_]+)$';
end;

function Process(e: IInterface): Integer;
begin
  if Signature(e) <> 'ARMO' then Exit;

  re_armor.Subject := EditorID(e);
  if re_armor.Match then
    armors.Add(re_armor.Groups[0]);
end;

function Finalize: Integer;
var
  i: Integer;
begin
  armors.Sort;
  for i := 0 to Pred(armors.Count) do
    AddMessage(armors[i]);
  armors.Free;
end;

end.
