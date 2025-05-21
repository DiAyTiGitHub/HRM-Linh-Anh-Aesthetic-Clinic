import { observer } from "mobx-react";
import { React, memo, useState } from "react";
import { Tooltip } from '@material-ui/core'
import DeleteIcon from '@material-ui/icons/Delete';
import { useFormikContext } from "formik";
import { useTranslation } from 'react-i18next';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

function SubTaskDetailItemDelete(props) {
    const {
        detailItemIndex,
        subTaskIndex,
        detail
    } = props;

    const { setFieldValue, values } = useFormikContext();
    const { t } = useTranslation();

    function confirmDeleteSubtaskItem() {
        const newItems = values?.subTasks[subTaskIndex]?.items;
        newItems?.splice(detailItemIndex, 1);

        setFieldValue(`subTasks[${subTaskIndex}].items`, newItems);
    }

    function isSubTaskItemEmpty() {
        if (detail?.name && detail?.name?.length > 0) return false;
        if (detail?.description && detail?.description?.length > 0) return false;
        if (detail?.staffs && detail?.staffs?.length > 0) return false;
        if (detail?.startTime) return false;
        if (detail?.endTime) return false;

        return true;
    }

    const [openConfirmDelete, setOpenConfirmDelete] = useState(false);

    function handleDeleteDetailItem() {
        if (isSubTaskItemEmpty()) {
            confirmDeleteSubtaskItem();
            return;
        }

        setOpenConfirmDelete(true);
    }

    return (
        <>
            <Tooltip title="Xóa chi tiết công việc con" placement="top">
                <div className="iconWrapper">
                    <DeleteIcon
                        className="my-0 mx-4 subTaskDetailIcon"
                        onClick={handleDeleteDetailItem}
                    />
                </div>
            </Tooltip>

            {openConfirmDelete && (
                <GlobitsConfirmationDialog
                    open={openConfirmDelete}
                    onConfirmDialogClose={() => setOpenConfirmDelete(false)}
                    onYesClick={confirmDeleteSubtaskItem}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa hạng mục trong công việc con này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    );
}

export default memo(observer(SubTaskDetailItemDelete));