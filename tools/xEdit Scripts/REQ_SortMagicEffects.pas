unit SortMagicEffects;

uses REQ_Util;


function Initialize: Integer;
begin
end;

function Process(e: IInterface): Integer;
var
  movedElement: Boolean;
  i, val1, val2: Integer;
  effects, effect1, effect2, effectLink: IInterface;
begin
  if (Signature(e) <> 'ALCH') and (Signature(e) <> 'ENCH') and (Signature(e) <> 'SCRL') and (Signature(e) <> 'SPEL') then
    Exit;
  effects := ElementByPath(e, 'Effects');
  while True do begin
    movedElement := False;
    for i := 0 to ElementCount(effects) - 2 do begin
      effect1 := ElementByIndex(effects, i);
      effect2 := ElementByIndex(effects, i + 1);
      val1 := GetSortKey(effect1);
      val2 := GetSortKey(effect2);
      if val2 < val1 then begin
        MoveDown(effect1);
        movedElement := True;
      end;
    end;
    if not movedElement then
      Break;
  end;
end;

function Finalize: Integer;
begin
end;

function GetSortKey(e: IInterface): Integer;
var
  baseEffect: IInterface;
  archtype: String;
begin
  baseEffect := WinningOverride(LinksTo(ElementByPath(e, 'EFID - Base Effect')));
  archtype := GetElementEditValues(baseEffect, 'Magic Effect Data\DATA - Data\Archtype');
  if SameText(archtype, 'Peak Value Modifier') or SameText(archtype, 'Value Modifier') then
    Result := GetElementNativeValues(baseEffect, 'Magic Effect Data\DATA - Data\Actor Value')
  else if SameText(archtype, 'Dual Value Modifier') then begin
    Result := GetElementNativeValues(baseEffect, 'Magic Effect Data\DATA - Data\Actor Value');
    if Result = 60 then  // Fame
      Result := GetElementNativeValues(baseEffect, 'Magic Effect Data\DATA - Data\Second Actor Value');
  end;
  if not Assigned(Result)
    then Result := 164;
end;

end.
