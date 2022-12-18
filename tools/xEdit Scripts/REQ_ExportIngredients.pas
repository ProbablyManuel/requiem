unit ExportIngredients;

var
  ingredients: TStringList;


function Initialize: Integer;
begin
  ingredients := TStringList.Create;
end;


function Process(e: IInterface): Integer;
var
  i, duration: Integer;
  magnitude: Float;
  effectName, ingredientName: String;
  effect, entry, entryArray: IInterface;
begin
  if Signature(e) <> 'INGR' then
    Exit;
  ingredientName := GetElementNativeValues(e, 'FULL - Name');
  entryArray := ElementByPath(e, 'Effects');
  for i := 0 to Pred(ElementCount(entryArray)) do begin
    entry := ElementByIndex(entryArray, i);
    effect := LinksTo(ElementByPath(entry, 'EFID - Base Effect'));
    effectName := GetElementNativeValues(effect, 'FULL - Name');
    magnitude := GetElementNativeValues(entry, 'EFIT - EFIT\Magnitude');
    duration := GetElementNativeValues(entry, 'EFIT - EFIT\Duration');
    ingredients.Add(Format('%s,%6.2f,%3d,%s', [effectName, magnitude, duration, ingredientName]));
  end;
end;


function Finalize: Integer;
var
  dlgSave: TSaveDialog;
begin
  if ingredients.Count > 0 then begin
    ingredients.Sort;
    dlgSave := TSaveDialog.Create(nil);
    dlgSave.Options := dlgSave.Options + [ofOverwritePrompt];
    dlgSave.InitialDir := DataPath;
    dlgSave.FileName := 'Ingredients.csv';
    if dlgSave.Execute then
      ingredients.SaveToFile(dlgSave.FileName);
    dlgSave.Free;
  end;
  ingredients.Free;
end;

end.
