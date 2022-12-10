unit CreateTemperedItem;

uses REQ_Util;

var
  minItemCondition, maxItemCondition: Integer;
  re_armor, re_weapon: TPerlRegEx;


function Initialize: Integer;
var
  sMaxItemCondition, sMinItemCondition: String;
begin
  InputQuery('Enter', 'Min Item Condition', sMinItemCondition);
  InputQuery('Enter', 'Max Item Condition', sMaxItemCondition);
  minItemCondition := StrToInt(sMinItemCondition);
  maxItemCondition := StrToInt(sMaxItemCondition);

  re_armor := TPerlRegEx.Create;
  re_armor.RegEx := '^([^_]+)_((Heavy|Light)_([^_]+)_([^_]+))?$';

  re_weapon := TPerlRegEx.Create;
  re_weapon.RegEx := '^([^_]+)_(Weapon_([^_]+)_([^_]+))?$';
end;


function Process(e: IInterface): Integer;
var
  leveledItem, entryArray, entry, extra: IInterface;
  i: Integer;
  leveledItemEditorId: String;
  re: TPerlRegEx;
begin
  if Signature(e) = 'ARMO' then
    re := re_armor
  else if Signature(e) = 'WEAP' then
    re := re_weapon
  else
    Exit;

  re.Subject := EditorID(e);
  if not re.Match then begin
    AddMessage(Name(e) + ' has invalid EditorID');
    Exit;
  end;

  leveledItem := AddRecordToFile(GetFile(e), 'LVLI');
  if minItemCondition = maxItemCondition then
    SetEditorID(leveledItem, Format('%s_LI_%s_%d', [re.Groups[1], re.Groups[2], minItemCondition]))
  else
    SetEditorID(leveledItem, Format('%s_LI_%s_%d_%d', [re.Groups[1], re.Groups[2], minItemCondition, maxItemCondition]));
  entryArray := Add(leveledItem, 'Leveled List Entries', True);
  RemoveByIndex(entryArray, 0, True);
  i := minItemCondition;
  while i <= maxItemCondition do begin
    entry := ElementAssign(entryArray, HighInteger, nil, False);
    SetElementEditValues(entry, 'LVLO - Base Data\Level', '1');
    SetElementEditValues(entry, 'LVLO - Base Data\Reference', IntToHex(GetLoadOrderFormID(e), 8));
    SetElementEditValues(entry, 'LVLO - Base Data\Count', '1');
    extra := Add(entry, 'COED', True);
    SetElementNativeValues(extra, 'Item Condition', i / 100);
    i := i + 10;
  end;
end;


function Finalize: Integer;
begin
  re_armor.Free;
  re_weapon.Free;
end;

end.
