import React, { memo, useState } from "react";
import { useFormikContext } from "formik";
import {
    Button,
    ButtonGroup,
    Grid,
} from "@material-ui/core";
import { observer } from "mobx-react";
import AddIcon from '@material-ui/icons/Add';
import { useTranslation } from "react-i18next";
import ResultStaffsTable from "./ResultStaffsTable/ResultStaffsTable";
import ChooseResultStaffsPopup from "./ResultStaffsTable/ChooseResultStaffsPopup";
import TouchAppIcon from '@material-ui/icons/TouchApp';

/*
*   Select multiple staffs
*/
function TabResultStaffs(props) {
    const { t } = useTranslation();

    const [openChooseStaff, setOpenChooseStaff] = useState(false);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <Grid container alignItems="center" justifyContent="space-between">
                    {/* Label hiển thị bên trái */}
                    <Grid item>
                        <p style={{ margin: 0, fontSize: "14px", fontWeight: "bold" }}>
                            Danh sách nhân viên:
                        </p>
                    </Grid>
                    {/* ButtonGroup hiển thị bên phải */}
                    <Grid item>
                        <ButtonGroup
                            color="container"
                            aria-label="outlined primary button group"
                        >
                            <Button
                                startIcon={<TouchAppIcon />}
                                type="button"
                                onClick={() => setOpenChooseStaff(true)}
                            >
                                Chọn nhân viên
                            </Button>
                        </ButtonGroup>
                    </Grid>
                </Grid>
            </Grid>



            <Grid item xs={12} style={{ overflowX: "auto" }}>
                <ResultStaffsTable />
            </Grid>

            {
                openChooseStaff && (
                    <ChooseResultStaffsPopup
                        open={openChooseStaff}
                        isDisableFilter={props?.isDisableFilter}
                        handleClose={() => setOpenChooseStaff(false)}
                        searchObject={props?.searchObject}
                    />
                )
            }
        </Grid>
    );
}

export default memo(observer(TabResultStaffs));
