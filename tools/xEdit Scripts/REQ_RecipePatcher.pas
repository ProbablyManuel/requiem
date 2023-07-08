unit RecipePatcher;

uses REQ_Util;

var
  re_recipe, re_recipe_artifact: TPerlRegEx;
  recipes_ingredients, recipes_conditions: TStringList;


function Initialize: Integer;
begin
  recipes_ingredients := TStringList.Create;
  recipes_ingredients.LoadFromFile('Edit Scripts\REQ_RecipePatcherIngredients.txt');

  recipes_conditions := TStringList.Create;
  recipes_conditions.LoadFromFile('Edit Scripts\REQ_RecipePatcherConditions.txt');

  re_recipe := TPerlRegEx.Create;
  re_recipe.RegEx := '^[^_]+_((?:Forge|Skyforge|Smelter|Rack|Temper(?:_Var)?)_(?:Heavy|Light|Weapon)_([^_]+)_([^_]+))(?:_(.+))?$';

  re_recipe_artifact := TPerlRegEx.Create;
  re_recipe_artifact.RegEx := '^[^_]+_(Temper_Artifact_.+)$';
end;

function Process(e: IInterface): integer;
var
  key: String;
begin
  if Signature(e) <> 'COBJ' then Exit;

  re_recipe.Subject := EditorID(e);
  re_recipe_artifact.Subject := EditorID(e);
  if re_recipe.Match then begin
    key := re_recipe.Groups[1];
    if Pos('Temper_Var', key) = 1 then
      // Remove "_Var" modifier
      key := Delete(key, 7, 4);
  end
  else if re_recipe_artifact.Match then
    key := re_recipe_artifact.Groups[1]
  else begin
    AddMessage('EditorID ' + EditorID(e) + ' is not a crafting/temper/breakdown recipe');
    Exit;
  end;
  if (recipes_ingredients.IndexOfName(key) = -1) then begin
    AddMessage('EditorID ' + EditorID(e) + ' is not recognized');
    Exit;
  end;
  if (recipes_conditions.IndexOfName(key) = -1) then begin
    AddMessage('EditorID ' + EditorID(e) + ' is not recognized');
    Exit;
  end;
  if (Pos('Smelter', key) = 1) or (Pos('Rack', key) = 1) then
    SetCreatedObject(e, key)
  else
    SetIngredients(e, key);
  SetConditions(e, key);
end;

function Finalize: Integer;
begin
  recipes_ingredients.Free;
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
  ingredients.StrictDelimiter := True;
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

procedure SetConditions(e: IInterface; recipe: String);
var
  items, entry: IInterface;
  i: Integer;
  conditions: TStringList;
begin
  conditions := TStringList.Create;
  conditions.StrictDelimiter := True;
  conditions.DelimitedText := recipes_conditions.Values[recipe];
  for i := 0 to Pred(conditions.Count div 6) do begin
    if conditions[6 * i + 3] = 'Items\Item\CNTO - Item\Item' then
      conditions[6 * i + 3] := IntToHex(GetElementLoadOrderFormID(e, conditions[6 * i + 3]), 8)
    else if conditions[6 * i + 3] <> '00 00 00 00' then
      conditions[6 * i + 3] := IntToHex(PairToLoadOrderFormID(conditions[6 * i + 3]), 8);
  end;

  items := ElementByPath(e, 'Conditions');
  if SerializeConditions(items) <> conditions.DelimitedText then begin
    RemoveElement(e, items);
    items := Add(e, 'Conditions', True);
    RemoveElement(items, 0);
    for i := 0 to Pred(conditions.Count div 6) do begin
      entry := ElementAssign(items, HighInteger, nil, False);
      SetElementEditValues(entry, 'CTDA\Type', conditions[6 * i]);
      SetElementEditValues(entry, 'CTDA\Comparison Value', conditions[6 * i + 1]);
      SetElementEditValues(entry, 'CTDA\Function', conditions[6 * i + 2]);
      SetElementEditValues(entry, 'CTDA\Parameter #1', conditions[6 * i + 3]);
      SetElementEditValues(entry, 'CTDA\Parameter #2', conditions[6 * i + 4]);
      SetElementEditValues(entry, 'CTDA\Run On', conditions[6 * i + 5]);
    end;
  end;
  conditions.Free;
end;

procedure SetCreatedObject(e: IInterface; recipe: String);
var
  createdObject, createdObjectCount: String;
  i: Integer;
  ingredients: TStringList;
begin
  ingredients := TStringList.Create;
  ingredients.StrictDelimiter := True;
  ingredients.DelimitedText := recipes_ingredients.Values[recipe];
  ingredients[0] := IntToHex(PairToLoadOrderFormID(ingredients[0]), 8);

  if IntToHex(GetElementLoadOrderFormID(e, 'CNAM - Created Object'), 8) <> ingredients[0] then
    SetElementEditValues(e, 'CNAM - Created Object', ingredients[0]);
  if GetElementEditValues(e, 'NAM1 - Created Object Count') <> ingredients[1] then
    SetElementEditValues(e, 'NAM1 - Created Object Count', ingredients[1]);

  ingredients.Free;
end;

function SerializeItems(items: IInterface): String;
var
  entry: IInterface;
  i, nativeFormID: Integer;
begin
  for i := 0 to Pred(ElementCount(items)) do begin
    entry := ElementByIndex(items, i);
    Result := Result + ',' + IntToHex(GetElementLoadOrderFormID(entry, 'CNTO - Item\Item'), 8);
    Result := Result + ',' + GetElementEditValues(entry, 'CNTO - Item\Count');
    if ElementExists(entry, 'COED - Extra Data') then
      Result := 'COED';
  end;
  Result := Delete(Result, 1, 1);
end;

function SerializeConditions(conditions: IInterface): String;
var
  c: IInterface;
  i, nativeFormID: Integer;
begin
  for i := 0 to Pred(ElementCount(conditions)) do begin
    c := ElementByIndex(conditions, i);
    Result := Result + ',' + GetElementEditValues(c, 'CTDA\Type');
    Result := Result + ',' + GetElementEditValues(c, 'CTDA\Comparison Value');
    Result := Result + ',' + GetElementEditValues(c, 'CTDA\Function');
    if Assigned(LinksTo(ElementByPath(c, 'CTDA\Parameter #1'))) then
      Result := Result + ',' + IntToHex(GetElementLoadOrderFormID(c, 'CTDA\Parameter #1'), 8)
    else
      Result := Result + ',' + GetElementEditValues(c, 'CTDA\Parameter #1');
    // The edit value of a quest stage is preceded by its log entry which is not the expected behavior
    if GetElementEditValues(c, 'CTDA\Parameter #2') <> '00 00 00 00' then
      Result := Result + ',' + IntToStr(GetElementNativeValues(c, 'CTDA\Parameter #2'))
    else
      Result := Result + ',' + GetElementEditValues(c, 'CTDA\Parameter #2');
    Result := Result + ',' + GetElementEditValues(c, 'CTDA\Run On');
  end;
  Result := Delete(Result, 1, 1);
end;

end.
