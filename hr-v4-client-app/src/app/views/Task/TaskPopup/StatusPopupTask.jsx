import React, { memo, useEffect } from "react";
import {Button, FormHelperText, Icon, IconButton} from "@material-ui/core";
import { useTranslation } from "react-i18next";
import "../_task.scss";
import { useState } from "react";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { useStore } from "app/stores"; 
import Popover from '@material-ui/core/Popover';
import VerticalSplitIcon from '@material-ui/icons/VerticalSplit';

function StatusPopup(props) {
    const { classButton } = props;

    const { t } = useTranslation();
    const { values, setFieldValue, errors, touched } = useFormikContext();
    const { taskStore } = useStore();
    const {
        optionWorkingStatus,
        getAllWorkingStatus
    } = taskStore;

    const [open, setOpen] = useState(false);

    //handle set default values for status is TODO if this task is new
    // console.log("optionWorkingStatus: ", optionWorkingStatus);
    // console.log("values: ", values);
    // useEffect(function () {
    //     //if this task is new, then set default status is TODO
    //     if (open && !values?.id) {
    //         optionWorkingStatus?.forEach(function (status) {
    //             console.log("looping status: ", status);
    //             if (status?.name == "Todo" || status?.statusValue == 0) {
    //                 setFieldValue('status', status);
    //             }
    //         });
    //     }
    // }, [values?.id, open]);

    function handleChangeStatus(item) {
        setFieldValue('status', item);
        setOpen(null);
    }

    //render working status if it's not cached in store
    useEffect(function () {
        if (!optionWorkingStatus || optionWorkingStatus?.length == 0) {
            getAllWorkingStatus();
        }
    }, []);

    return (
        <>
            <Button
                className={`${classButton} ${errors.status && touched.status ? "bgc-danger-tp1" : ""}`}
                variant="contained"
                startIcon={<VerticalSplitIcon />}
                onClick={(event) => setOpen(event.currentTarget)}
            >
                Trạng thái thực hiện: <b >{` ${values?.status?.name || 'Chưa đặt'}`}</b>
            </Button>
            <Popover
                anchorEl={open}
                open={Boolean(open)}
                onClose={() => setOpen(null)}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'center',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'center',
                }}
            >
                <div className="p-8">
                    <div style={{ position: 'relative', borderBottom: "1px solid #dee2e6", paddingBottom: 5 }}>
                        <p style={{ textAlign: "center", margin: 0, color: "#5e6c84" }}>{t("task.status")}</p>
                        <IconButton
                            style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
                            onClick={() => setOpen(null)}
                        >
                            <Icon color="disabled" title={"Đóng"} fontSize="small" >
                                close
                            </Icon>
                        </IconButton>
                    </div>

                    <div
                        style={{ maxHeight: '250px', overflow: 'auto', minWidth: 250 }}
                        className="styledThinScrollbar mt-8"
                    >
                        {optionWorkingStatus?.map((item, index) => {
                            const check = values?.status ? values?.status?.id === item?.id : false;

                            return (
                                <p
                                    key={index}
                                    className="Member d-flex flex-middle" style={{ cursor: 'pointer' }}
                                    onClick={() => handleChangeStatus(item)}
                                >
                                    {item?.name} {check && <CheckIcon style={{ height: '15px' }} color='primary' />}
                                </p>
                            )
                        })}

                    </div>
                </div>
            </Popover>
        </>
    );
}

export default memo(observer(StatusPopup));