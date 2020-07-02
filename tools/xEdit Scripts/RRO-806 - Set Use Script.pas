unit SetUseScript;


function Initialize: Integer;
begin

end;


function Process(e: IInterface): Integer;
var
  template: IInterface;
begin
  if SameText(Signature(e), 'NPC_') then begin
    if ElementExists(e, 'TPLT') then begin
      template := LinksTo(ElementByPath(e, 'TPLT'));
      if SameText(Signature(template), 'NPC_') then begin
        if ElementExists(e, 'VMAD') then begin
          if GetElementNativeValues(e, 'ACBS\Template Flags\Use Script') then begin
            SetElementNativeValues(e, 'ACBS\Template Flags\Use Script', False);
          end
        end
        else begin
          if not GetElementNativeValues(e, 'ACBS\Template Flags\Use Script') then begin
            SetElementNativeValues(e, 'ACBS\Template Flags\Use Script', True);
          end;
        end;
      end
      else if SameText(Signature(template), 'LVLN') then begin
        if GetElementNativeValues(e, 'ACBS\Template Flags\Use Script') then begin
          SetElementNativeValues(e, 'ACBS\Template Flags\Use Script', False);
        end;
      end;
    end;
  end;
end;


function Finalize: Integer;
begin

end;

end.

