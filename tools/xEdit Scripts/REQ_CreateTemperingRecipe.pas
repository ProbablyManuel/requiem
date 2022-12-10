unit CreateTemperingRecipe;

uses REQ_Util;


function Initialize: Integer;
begin

end;


function Process(e: IInterface): Integer;
var
  workbenchKeyword: String;
  recipe: IInterface;
begin
  if Signature(e) = 'ARMO' then
    workbenchKeyword := '000ADB78'
  else if Signature(e) = 'WEAP' then
    workbenchKeyword := '00088108'
  else
    Exit;
  
  recipe := AddRecordToFile(GetFile(e), 'COBJ');
  SetElementEditValues(recipe, 'BNAM', workbenchKeyword);
  SetElementEditValues(recipe, 'CNAM', IntToHex(GetLoadOrderFormID(e), 8));
  SetElementEditValues(recipe, 'NAM1', '1');
end;


function Finalize: Integer;
begin

end;

end.
