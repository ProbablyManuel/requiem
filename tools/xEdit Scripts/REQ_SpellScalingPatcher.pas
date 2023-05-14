unit SpellScalingPatcher;

uses REQ_Util;

var
  scaling_flags: TStringList;
  re_effect, re_spell: TPerlRegEx;


function Initialize: Integer;
begin
  scaling_flags := TStringList.Create;
  scaling_flags.LoadFromFile('Edit Scripts\REQ_SpellScaling.txt');

  re_effect := TPerlRegEx.Create;
  re_effect.RegEx := '^([^_]+)_(Alteration|Conjuration|Destruction|Illusion|Restoration)([0-5X])_([^_]+)_([^_]+)';

  re_spell := TPerlRegEx.Create;
  re_spell.RegEx := '^([^_]+)_(Alteration|Conjuration|Destruction|Illusion|Restoration)([0-5])_([^_]+)$';
end;

function Process(e: IInterface): Integer;
begin
  ProcessEffect(e);
  ProcessSpell(e);
end;

function Finalize: Integer;
begin
  scaling_flags.Free;
  re_effect.Free;
  re_spell.Free;
end;

procedure ProcessEffect(e: IInterface);
var
  spell: String;
  flags: TStringList;
begin
  if Signature(e) <> 'MGEF' then Exit;

  re_effect.Subject := EditorID(e);
  if not re_effect.Match then begin
    AddMessage(Name(e) + ' is invalid');
    Exit;
  end;

  spell := re_effect.Groups[2] + re_effect.Groups[3] + '_' + re_effect.Groups[4];

  flags := TStringList.Create;
  flags.StrictDelimiter := True;
  flags.DelimitedText := scaling_flags.Values[spell];
  if flags.Count = 0 then begin
    AddMessage(Name(e) + ' is not recognized');
    Exit;
  end;

  if GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\Power Affects Magnitude') <> flags[0] then
    SetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\Power Affects Magnitude', flags[0]);
  if GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\Power Affects Duration') <> flags[1] then
    SetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\Power Affects Duration', flags[1]);
  if GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\No Magnitude') <> flags[2] then
    SetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\No Magnitude', flags[2]);
  if GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\No Duration') <> flags[3] then
    SetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\No Duration', flags[3]);
  if flags[4] = '1' then
    AddKeyword(e, 'REQ_NoMagnitudeScaling')
  else
    RemoveKeyword(e, 'REQ_NoMagnitudeScaling');
  if flags[5] = '1' then
    AddKeyword(e, 'REQ_NoDurationScaling')
  else
    RemoveKeyword(e, 'REQ_NoDurationScaling');

  flags.Free;
end;

procedure ProcessSpell(e: IInterface);
var
  spell: String;
  flags: TStringList;
  i: Integer;
  effects, effect: IInterface;
begin
  if Signature(e) <> 'SPEL' then Exit;

  re_spell.Subject := EditorID(e);
  if not re_spell.Match then Exit;

  spell := re_spell.Groups[2] + re_spell.Groups[3] + '_' + re_spell.Groups[4];

  flags := TStringList.Create;
  flags.StrictDelimiter := True;
  flags.DelimitedText := scaling_flags.Values[spell];
  if flags.Count = 0 then begin
    AddMessage(Name(e) + ' is not recognized');
    Exit;
  end;

  if GetElementEditValues(e, 'SPIT - Data\Flags\No Dual Cast Modification') <> flags[6] then
    SetElementEditValues(e, 'SPIT - Data\Flags\No Dual Cast Modification', flags[6]);

  if (flags[0] = '0') and (flags[1] = '0') and (flags[6] = '1') then
    AddMessage(Name(e) + ' has no power scaling but can be dualcast');

  // effects := ElementByPath(e, 'Effects');
  // for i := 0 to Pred(ElementCount(effects)) do begin
  //   effect := ElementByIndex(effects, i);
  //   if (GetElementNativeValues(effect, 'EFIT - EFIT\Magnitude') = 0.0) and (flags[2] = '0') then
  //     AddMessage(Name(e) + ' has magnitude zero but lacks the flag No Magnitude');
  //   if (GetElementNativeValues(effect, 'EFIT - EFIT\Duration') = 0) and (flags[3] = '0') then
  //     AddMessage(Name(e) + ' has duration zero but lacks the flag No Duration');
  // end;

  flags.Free;
end;

end.
