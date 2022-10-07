unit RecipePatcher;

uses REQ_Lookup;

var
  re_ignore, re_recipe, re_recipe_artifact: TPerlRegEx;
  recipes_ingredients: TStringList;


function Initialize: Integer;
begin
  recipes_ingredients := TStringList.Create;
  recipes_ingredients.LoadFromFile('Edit Scripts\REQ_RecipePatcherIngredients.txt');

  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^[^_]+_(DEPRECATED|LEGACY|NULL|AetheriumForge|Oven|Cook|Ench|Mill|Rack|Smelter|Skyforge_Arrow|Forge_(?:Amulet|Arrow|Bolt|Circlet|Ench|Ring|Staff))_.+$';

  re_recipe := TPerlRegEx.Create;
  re_recipe.RegEx := '^[^_]+_((?:Forge|Skyforge|Temper(?:_Var)?)_(?:Heavy|Light|Weapon)_([^_]+)_([^_]+))(?:_(.+))?$';

  re_recipe_artifact := TPerlRegEx.Create;
  re_recipe_artifact.RegEx := '^[^_]+_(Temper_Artifact_.+)$';
end;

function Process(e: IInterface): integer;
var
  key: String;
begin
  if Signature(e) <> 'COBJ' then Exit;
  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then Exit;

  re_recipe.Subject := EditorID(e);
  re_recipe_artifact.Subject := EditorID(e);
  if re_recipe.Match then  begin
    key := re_recipe.Groups[1];
    if Pos('Temper_Var', key) = 1 then
      // Remove "_Var" modifier
      key := Delete(key, 7, 4);
  end
  else if re_recipe_artifact.Match then
    key := re_recipe_artifact.Groups[1]
  else begin
    AddMessage('EditorID ' + EditorID(e) + ' is invalid');
    Exit;
  end;
  if recipes_ingredients.IndexOfName(key) = -1 then begin
    AddMessage('EditorID ' + EditorID(e) + ' is not recognized');
    Exit;
  end;
  SetIngredients(e, key);
end;

function Finalize: Integer;
begin
  recipes_ingredients.Free;
  re_ignore.Free;
  re_recipe.Free;
  re_recipe_artifact.Free;
end;

procedure SetIngredients(e: IInterface; recipe: String);
var
  items, entry: IInterface;
  i: Integer;
  ingredients: TStringList;
begin
  ingredients := TStringList.Create;
  ingredients.DelimitedText := recipes_ingredients.Values[recipe];
  for i := 0 to Pred(ingredients.Count div 2) do begin
    ingredients[i * 2] := IntToHex(PairToLoadOrderFormID(ingredients[2 * i]), 8);
  end;

  items := ElementByPath(e, 'Items');
  if SerializeItems(items) <> ingredients.DelimitedText then begin
    RemoveElement(e, items);
    items := Add(e, 'Items', True);
    RemoveElement(items, 0);
    for i := 0 to Pred(ingredients.Count div 2) do begin
      entry := ElementAssign(items, HighInteger, nil, False);
      SetElementEditValues(entry, 'CNTO - Item\Item', ingredients[2 * i]);
      SetElementEditValues(entry, 'CNTO - Item\Count', ingredients[2 * i + 1]);
    end;
  end;
  ingredients.Free;
end;

function SerializeItems(items: IInterface): String;
var
  entry: IInterface;
  i, nativeFormID: Integer;
begin
  for i := 0 to Pred(ElementCount(items)) do begin
    entry := ElementByIndex(items, i);
    Result := Result + ',' + IntToHex(FileFormIDtoLoadOrderFormID(GetFile(items), GetElementNativeValues(entry, 'CNTO - Item\Item')), 8);
    Result := Result + ',' + GetElementEditValues(entry, 'CNTO - Item\Count');
    if ElementExists(entry, 'COED - Extra Data') then
      Result := 'COED';
  end;
  Result := Delete(Result, 1, 1);
end;

end.