import React, { memo, useState } from "react";
import { observer } from "mobx-react";
import {
    DialogContent,
    Grid,
    Button,
    Tooltip,
} from "@material-ui/core";
import { useTranslation } from "react-i18next";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import ChoosingParentPositionTitleList from "./ChoosingParentPositionTitleList";
import FilterParentPositionTitle from "./FilterParentPositionTitle";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function ChoosingParentPositionTitlePopup(props) {
    const { labelPopup = "Lựa chọn nhóm ngạch" } = props;
    const { t } = useTranslation();

    const [openPopup, setOpenPopup] = useState(false);

    function handleClosePopup() {
        setOpenPopup(false);
    }

    return (
        <>
            <Tooltip placement="top" title={t("general.button.select") + " nhóm ngạch"}>
                <Button
                    fullWidth
                    variant="contained"
                    className="btn bgc-lighter-dark-blue text-white mt-25 px-0"
                    onClick={() => setOpenPopup(true)}
                >
                    <TouchAppIcon className="text-white" />
                </Button>
            </Tooltip>

            {openPopup && (
                <GlobitsPopupV2
                    size="md"
                    scroll={"body"}
                    open={openPopup}
                    noDialogContent
                    title={t("general.button.select") + " nhóm ngạch"}
                    onClosePopup={handleClosePopup}
                    popupId={"popupselectgrouppost"}
                >
                    <DialogContent
                        className="o-hidden p-12"
                        style={{ maxHeight: "80vh" }}
                    >
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <FilterParentPositionTitle
                                    openPopup={openPopup}
                                />
                            </Grid>

                            <Grid item xs={12}>
                                <ChoosingParentPositionTitleList
                                    handleClose={handleClosePopup}
                                />
                            </Grid>
                        </Grid>
                    </DialogContent>
                </GlobitsPopupV2 >
            )}
        </>
    );
}

export default memo(observer(ChoosingParentPositionTitlePopup));
