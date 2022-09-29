unit NameRecipe;

var
  prefix: String;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'Prefix', prefix) then
    Result := -1;
end;

function Process(e: IInterface): integer;
var
  createdObject: IInterface;
  sig, newEditorID, workbench: String;
begin
  if Signature(e) <> 'COBJ' then Exit;

  workbench := GetWorkbench(e);
  if workbench = 'NULL' then
    newEditorID := prefix + '_NULL_' + EditorID(MasterOrSelf(e))
  else begin
    createdObject := WinningOverride(LinksTo(ElementByPath(e, 'CNAM - Created Object')));
    sig := Signature(createdObject);
    if (workbench = 'Smelter') or (workbench = 'Rack') then
      if (sig <> 'ALCH') and (sig <> 'SCRL') and (sig <> 'SLGM') then
        createdObject := WinningOverride(LinksTo(ElementByPath(e, 'Items\Item\CNTO - Item\Item')));
    newEditorID := EditorID(createdObject);
    if pos(prefix + '_', newEditorID) = 1 then
      Delete(newEditorID, 1, Length(prefix) + 1);
    newEditorID := prefix + '_' + workbench + '_' + newEditorID;
  end;
  SetEditorID(e, newEditorID);
end;

function Finalize: Integer;
begin

end;


function GetWorkbench(e: IInterface): String;
var
  keyword: Integer;
begin
  keyword := GetElementNativeValues(e, 'BNAM - Workbench Keyword');
  case keyword of
    $00088105: Result := 'Forge';
    $000F46CE: Result := 'Skyforge';
    $000ADB78: Result := 'Temper';  // Armor Table
    $00088108: Result := 'Temper';  // Sharpening Wheel
    $000A5CCE: Result := 'Smelter';
    $0007866A: Result := 'Rack';
    $000A5CB3: Result := 'Cook';
    $0009C6DE: Result := 'Mill';
    $02005756: Result := 'AetheriumForge';
    $030117F7: Result := 'Oven';
    $04017738: Result := 'Ench';  // Staff Enchanter
    $0AAD3B01: Result := 'NULL';
  else
    Result := '';
  end;
end;

end.