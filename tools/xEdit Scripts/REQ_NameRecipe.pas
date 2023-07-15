unit NameRecipe;

uses REQ_Util;

var
  prefix: String;
  slWorkbench: TStringList;


function Initialize: Integer;
begin
  if not InputQuery('Enter', 'Prefix', prefix) then
    Result := -1;

  slWorkbench := TStringList.Create;
  slWorkbench.Add('BYOHCraftingOven=Oven');
  slWorkbench.Add('CraftingCookpot=Cook');
  slWorkbench.Add('CraftingSmelter=Smelter');
  slWorkbench.Add('CraftingSmithingArmorTable=Temper');
  slWorkbench.Add('CraftingSmithingForge=Forge');
  slWorkbench.Add('CraftingSmithingSharpeningWheel=Temper');
  slWorkbench.Add('CraftingSmithingSkyforge=Skyforge');
  slWorkbench.Add('CraftingTanningRack=Rack');
  slWorkbench.Add('DLC1LD_CraftingForgeAetherium=AetheriumForge');
  slWorkbench.Add('DLC2StaffEnchanter=Ench');
  slWorkbench.Add('isGrainMill=Mill');
  slWorkbench.Add('REQ_DisableRecipe=NULL');
end;

function Process(e: IInterface): integer;
var
  createdObject: IInterface;
  sig, newEditorID, workbench, workbenchKeyword: String;
begin
  if Signature(e) <> 'COBJ' then Exit;

  workbenchKeyword := EditorID(LinksTo(ElementByPath(e, 'BNAM - Workbench Keyword')));
  workbench := slWorkbench.Values[workbenchKeyword];
  if workbench = 'NULL' then
    newEditorID := prefix + '_NULL_' + EditorID(MasterOrSelf(e))
  else begin
    createdObject := WinningOverride(LinksTo(ElementByPath(e, 'CNAM - Created Object')));
    sig := Signature(createdObject);
    if (workbench = 'Smelter') or (workbench = 'Rack') then
      if (sig <> 'ALCH') and (sig <> 'SCRL') and (sig <> 'SLGM') then
        createdObject := WinningOverride(GetBreakdownItem(e));
    newEditorID := EditorID(createdObject);
    if pos(prefix + '_', newEditorID) = 1 then
      Delete(newEditorID, 1, Length(prefix) + 1);
    newEditorID := prefix + '_' + workbench + '_' + newEditorID;
  end;
  SetEditorID(e, newEditorID);
end;

function Finalize: Integer;
begin
  slWorkbench.Free;
end;

function GetBreakdownItem(e: IInterface): IInterface;
var
  i: Integer;
  item, items: IInterface;
begin
  items := ElementByPath(e, 'Items');
  for i := 0 to Pred(ElementCount(items)) do begin
    item := LinksTo(ElementByPath(ElementByIndex(items, i), 'CNTO - Item\Item'));
    if GetLoadOrderFormID(item) <> PairToLoadOrderFormID('Requiem.esp:43D07D') then begin
      Result := item;
      Exit;
    end;
  end;
end;

end.
