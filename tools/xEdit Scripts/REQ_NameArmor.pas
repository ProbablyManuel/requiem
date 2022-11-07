unit NameArmor;

var
  armorSet: String;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'Armor Set', armorSet) then
    Result := -1;
end;

function Process(e: IInterface): Integer;
var
  newEditorId: String;
begin
  if Signature(e) <> 'ARMO' then
    Exit;
  newEditorId := Format('REQ_%s_%s_%s', [GetArmorType(e), armorSet, GetArmorPart(e)]);
  SetEditorID(e, newEditorId);
end;

function Finalize: Integer;
begin

end;

function GetArmorPart(e: IInterface): String;
begin
  if HasKeyword(e, 'ArmorHelmet') or HasKeyword(e, 'ClothingHead') then
    Result := 'Head'
  else if HasKeyword(e, 'ArmorCuirass') or HasKeyword(e, 'ClothingBody') then
    Result := 'Body'
  else if HasKeyword(e, 'ArmorGauntlets') or HasKeyword(e, 'ClothingHands') then
    Result := 'Hands'
  else if HasKeyword(e, 'ArmorBoots') or HasKeyword(e, 'ClothingFeet') then
    Result := 'Feet'
  else if HasKeyword(e, 'ArmorShield') then
    Result := 'Shield';
end;

function GetArmorType(e: IInterface): String;
begin
  if HasKeyword(e, 'ArmorHeavy') then
    Result := 'Heavy'
  else if HasKeyword(e, 'ArmorLight') then
    Result := 'Light'
  else if HasKeyword(e, 'ArmorClothing') then
    Result := 'Cloth';
end;

function HasKeyword(e: IInterface; s: String): Boolean;
var
  keywords: IInterface;
  i: integer;
begin
  keywords := ElementByPath(e, 'KWDA');
  for i := 0 to Pred(ElementCount(keywords)) do
    if EditorID(LinksTo(ElementByIndex(keywords, i))) = s then 
      Result := True;
end;

end.
