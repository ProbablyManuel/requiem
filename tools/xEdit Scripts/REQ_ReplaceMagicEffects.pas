unit ReplaceMagicEffect;

uses REQ_Util;

var
  replacements: TStringList;


function Initialize: Integer;
begin
  replacements := TStringList.Create;
  replacements.LoadFromFile('Edit Scripts\REQ_ReplaceMagicEffects.txt');
end;

function Process(e: IInterface): integer;
var
  replacementFormID: Cardinal;
  i: Integer;
  fileName, nativeFormID, formIDPair, replacement: String;
  effects, effect, effectLink: IInterface;
begin
  if (Signature(e) <> 'ALCH') and (Signature(e) <> 'ENCH') and (Signature(e) <> 'SCRL') and (Signature(e) <> 'SPEL') then
    Exit;
  effects := ElementByPath(e, 'Effects');
  for i := 0 to Pred(ElementCount(effects)) do begin
    effect := ElementByIndex(effects, i);
    effectLink := LinksTo(ElementByPath(effect, 'EFID - Base Effect'));
    fileName := GetFileName(GetFile(MasterOrSelf(effectLink)));
    nativeFormID := IntToHex(FormID(effectLink) and $FFFFFF, 6);
    formIDPair := Format('%s:%s', [fileName, nativeFormID]);
    replacement := replacements.Values[formIDPair];
    if replacement = '' then
      Continue;
    replacementFormID := LoadOrderFormIDtoFileFormID(GetFile(e), PairToLoadOrderFormID(replacement));
    SetElementNativeValues(effect, 'EFID - Base Effect', replacementFormID);
  end;
end;

function Finalize: Integer;
begin
  replacements.Free;
end;

end.
