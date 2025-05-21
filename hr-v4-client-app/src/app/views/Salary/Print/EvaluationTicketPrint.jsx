import { observer } from "mobx-react";
import { forwardRef, memo } from "react";
import PrintCommon from "../../../common/Print/PrintCommon";


import EvaluationTicketView from "app/views/Staff/EvaluationTicket/EvaluationTicketView";
function EvaluationTicketPrint({ printData, componentRef }, ref) {
    
    return (
        <PrintCommon ref={componentRef} marginPage='8mm' size='A4'>
            <EvaluationTicketView data={printData} />
        </PrintCommon>
    );
}

export default memo(observer(forwardRef(EvaluationTicketPrint)));
