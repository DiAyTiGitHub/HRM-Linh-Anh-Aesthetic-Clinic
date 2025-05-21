import {Tooltip} from '@material-ui/core'
import React, {memo, useEffect} from 'react'
import {useFormikContext} from 'formik';
import {useState} from 'react';
import DeleteIcon from '@material-ui/icons/Delete';
import {useTranslation} from 'react-i18next';
import {observer} from 'mobx-react';
import PostAddIcon from '@material-ui/icons/PostAdd';
import LinearLabelProgressBar from './LinearLabelProgressBar';
import SubTaskDetailItem from './SubTaskDetail/SubTaskDetailItem';
import GlobitsConfirmationDialog from 'app/common/GlobitsConfirmationDialog';
import GlobitsTextField from "../../../../common/form/GlobitsTextField";

const dataDefaultItemSubTask = {
    id: null,
    subTaskId: null,
    name: null,
    code: null,
    description: null,
    staffs: null,
    startTime: null,
    endTime: null,
    value: null,
    clientId: null,
}

function SubTaskItem(props) {
    const {
        subTask,
        subTaskIndex
    } = props;

    const {t} = useTranslation();
    const {values, setFieldValue, errors, touched} = useFormikContext();

    const [completePercentage, setCompletePercentage] = useState(0);

    useEffect(function () {
        let percentageCompleted = 0;

        let numOfCompletedItems = 0;
        subTask?.items?.forEach(function (item) {
            if (item?.value) numOfCompletedItems++;
        });

        if (subTask?.items?.length) {
            percentageCompleted = Array?.isArray(subTask?.items)
                ? numOfCompletedItems / (subTask?.items?.length) * 100 : null;
        }

        setCompletePercentage(percentageCompleted);
    }, [subTask, subTask?.items, subTask?.items?.length]);

    function handleChangeSubTaskName(e) {
        setFieldValue(`subTasks[${subTaskIndex}].name`, e?.target?.value);
    }

    function handleOpenAddDetailPopover() {
        const details = values?.subTasks[subTaskIndex]?.items || [];
        details.push(structuredClone(dataDefaultItemSubTask));

        setFieldValue(`subTasks[${subTaskIndex}].items`, details);
    }

    function confirmDeleteSubtask() {
        const newSubTasks = values?.subTasks;
        newSubTasks?.splice(subTaskIndex, 1);

        setFieldValue(`subTasks`, newSubTasks);
    }

    function isSubTaskEmpty() {
        if (subTask?.name && subTask?.name?.length > 0) return false;
        if (subTask?.description && subTask?.description?.length > 0) return false;
        if (subTask?.items && subTask?.items?.length > 0) return false;
        return true;
    }

    const [openConfirmDelete, setOpenConfirmDelete] = useState(false);

    function handleDeleteSubTask() {
        if (isSubTaskEmpty()) {
            confirmDeleteSubtask();
            return;
        }

        setOpenConfirmDelete(true);
    }

    return (
        <>
            <div className='subTaskItem'>
                <div className="flex space-between w-100 align-center" style={{alignItems: "flex-start"}}>
                    <div className="flex-1 my-0 w-100 mr-8">
                        <div className="subTaskName">
                            <GlobitsTextField
                                className="subTaskNameField"
                                name={`subTasks[${subTaskIndex}].name`}
                                placeholder={"Nội dung việc cần làm..."}
                                value={values?.subTasks[subTaskIndex].name}
                                onChange={handleChangeSubTaskName}
                                autoFocus
                            />
                        </div>

                        <LinearLabelProgressBar
                            value={completePercentage}
                        />
                    </div>


                    <div
                        className="subTaskActions flex flex-center"
                    >
                        <Tooltip placement='top' title="Thêm chi tiết cho công việc">
                            <PostAddIcon
                                className='addSubTaskDetailButton pl-0'
                                onClick={handleOpenAddDetailPopover}
                            />
                        </Tooltip>

                        <Tooltip placement='top' title="Xóa công việc con">
                            <DeleteIcon
                                className='deleteSubTaskButton pl-0'
                                onClick={handleDeleteSubTask}
                            />
                        </Tooltip>
                    </div>
                </div>


                <div className="subTaskItemContainer w-100 pt-8">
                    {subTask?.items && Array.isArray(subTask?.items) && (
                        subTask?.items.map(function (detailItem, index) {

                            return (
                                <SubTaskDetailItem
                                    key={index}
                                    detail={detailItem}
                                    detailItemIndex={index}
                                    subTaskIndex={subTaskIndex}
                                />
                            );
                        })
                    )}
                </div>
            </div>

            {openConfirmDelete && (
                <GlobitsConfirmationDialog
                    open={openConfirmDelete}
                    onConfirmDialogClose={() => setOpenConfirmDelete(false)}
                    onYesClick={confirmDeleteSubtask}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa công việc con này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>

    );
}

export default memo(observer(SubTaskItem));