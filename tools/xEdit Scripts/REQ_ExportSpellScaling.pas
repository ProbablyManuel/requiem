unit ExportSpellScaling;

uses REQ_Util;

var
  re_effect: TPerlRegEx;
  scaling_flags: TStringList;


function Initialize: Integer;
begin
  re_effect := TPerlRegEx.Create;
  re_effect.RegEx := '^([^_]+)_(Alteration|Conjuration|Destruction|Illusion|Restoration)([0-5])_([^_]+)_([^_]+)$';

  scaling_flags := TStringList.Create;
  scaling_flags.Add('Spell,Power Affects Magnitude,Power Affects Duration,No Magnitude,No Duration,No Magnitude Scaling,No Duration Scaling');
end;


function Process(e: IInterface): Integer;
var
  i: Integer;
  r: IInterface;
  values: TStringList;
begin
  if Signature(e) <> 'MGEF' then
    Exit;

  re_effect.Subject := EditorID(e);
  if not re_effect.Match then
    Exit;

  values := TStringList.Create;
  values.Delimiter := ',';
  values.Add(re_effect.Groups[2] + re_effect.Groups[3] + '_' + re_effect.Groups[4]);
  values.Add(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\Power Affects Magnitude'));
  values.Add(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\Power Affects Duration'));
  values.Add(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\No Magnitude'));
  values.Add(GetElementEditValues(e, 'Magic Effect Data\DATA - Data\Flags\No Duration'));
  values.Add(IntToStr(BoolToInt(HasKeyword(e, 'REQ_NoMagnitudeScaling'))));
  values.Add(IntToStr(BoolToInt(HasKeyword(e, 'REQ_NoDurationScaling'))));

  scaling_flags.Add(values.DelimitedText);

  values.Free;
end;


function Finalize: Integer;
var
  dlgSave: TSaveDialog;
  targetDir: String;
begin
  targetDir := DataPath + 'tools\xEdit Scripts\patcher_data\';
  scaling_flags.SaveToFile(targetDir + 'SpellScalingRaw.csv');
  scaling_flags.Free;
  re_effect.Free;
end;

end.
