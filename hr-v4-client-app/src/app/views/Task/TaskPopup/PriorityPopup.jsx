import React from "react";
import { Button, Icon, IconButton } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import "../_task.scss";
import LocalConstants from "app/LocalConstants";
import CheckIcon from '@material-ui/icons/Check';
import { useState } from "react";
import BookmarkIcon from "@material-ui/icons/Bookmark";
import { useFormikContext } from "formik";
import Popover from '@material-ui/core/Popover';

export default function PriorityPopup(props) {
    const { classButton } = props;

    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();
    const [openPriority, setOpenPriority] = useState();

    function handleChangePriority(item) {
        setFieldValue("priority", item?.id);
        setOpenPriority(null);
    }

    return (
        <div>
            <Button
                className={classButton}
                variant="contained"
                startIcon={<BookmarkIcon />}
                onClick={(event) => setOpenPriority(event.currentTarget)}
            >
                {t("task.priority")}:<b>
                    {values?.priority && LocalConstants?.Priority?.find(e => e?.id === values?.priority)?.name}
                </b>
            </Button>

            <Popover
                anchorEl={openPriority}
                open={Boolean(openPriority)}
                onClose={() => setOpenPriority(null)}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'center',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'center',
                }}
            >
                <div style={{ padding: 10, minWidth: "200px" }}>
                    <div style={{ position: 'relative', borderBottom: "1px solid #dee2e6", paddingBottom: 5, marginBottom: "10px" }}>
                        <p style={{ textAlign: "center", margin: 0 }}>{t("task.priority")}</p>
                        <IconButton
                            style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
                            onClick={() => setOpenPriority(null)}
                        >
                            <Icon color="disabled" title={"Đóng"} fontSize="small" >
                                close
                            </Icon>
                        </IconButton>
                    </div>
                    <div className="flex flex-column">
                        {LocalConstants.Priority.map((item, index) => (
                            <div key={index} className={`priorityTag ${item?.className} `} onClick={() => handleChangePriority(item)} >
                                {item?.name} {item?.id === values?.priority && <CheckIcon style={{ height: '15px' }} color='white' />}
                            </div>
                        ))}
                    </div>
                </div>
            </Popover>
        </div>

    );
}
