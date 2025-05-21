import React , { memo } from "react";
import { observer } from "mobx-react";
import { DialogContent , Grid , } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import DepartmentFilters from "app/views/Department/DepartmentFilters";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectDepartmentListInStaff from "app/views/HumanResourcesInformation/Component/SelectDepartmentListInStaff";

function SelectDepartmentPopupV2(props) {
    const {t} = useTranslation();

    const {
        open ,
        handleClosePopup ,
        setOpenDepartmentPopup ,
        organizationId ,
        name = 'department' ,
        clearFields ,
    } = props;

    const handleConfirmSelectDepartment = () => {
        //setOpenDepartmentPopup(false);
        handleClosePopup();
    };

    return (
        <GlobitsPopupV2
            size="md"
            scroll={"body"}
            open={open}
            noDialogContent
            title={t("Lựa chọn đơn vị")}
            onClosePopup={handleClosePopup}
            popupId={"popupselectdep"}
        >
            <DialogContent className="o-hidden p-12">
                <Grid className='index-card' container spacing={2}>
                    <Grid item xs={12}>
                        <DepartmentFilters organizationId={organizationId}/>
                    </Grid>
                    <Grid item xs={12} className={"pt-10"}>
                        <SelectDepartmentListInStaff
                            handleClose={handleConfirmSelectDepartment}
                            organizationId={organizationId}
                            name={name}
                            clearFields={clearFields}
                        /> </Grid>
                </Grid>
            </DialogContent>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SelectDepartmentPopupV2));
