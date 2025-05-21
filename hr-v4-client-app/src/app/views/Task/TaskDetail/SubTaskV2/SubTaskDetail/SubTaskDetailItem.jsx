import { observer } from "mobx-react";
import { React, memo } from "react";
import { Tooltip } from '@material-ui/core'
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { formatDate } from 'app/LocalFunction';
import ScheduleIcon from '@material-ui/icons/Schedule';
import PersonAddIcon from '@material-ui/icons/PersonAdd';
import DeleteIcon from '@material-ui/icons/Delete';
import GlobitsAvatar from 'app/common/GlobitsAvatar';
import { useFormikContext } from "formik";
import SubTaskDetailItemDoer from "./SubTaskDetailItemDoer";
import SubTaskDetailItemTime from "./SubTaskDetailItemTime";
import SubTaskDetailItemDelete from "./SubTaskDetailItemDelete";

function SubTaskDetailItem(props) {
    const {
        detail,
        detailItemIndex,
        subTaskIndex
    } = props;

    const { setFieldValue, values } = useFormikContext();

    function handleChangeCBItem(e) {
        setFieldValue(`subTasks[${subTaskIndex}].items[${detailItemIndex}].value`, e?.target?.checked);
    }

    function handleChangeSubTaskDetail(e) {
        setFieldValue(`subTasks[${subTaskIndex}].items[${detailItemIndex}].name`, e?.target?.value);
    }

    return (
        <div className='checkbox d-flex w-100 align-middle detailItemWrapper'>
            <GlobitsCheckBox
                className="p-6"
                name={`subTasks[${subTaskIndex}].items[${detailItemIndex}].value`}
                onChange={handleChangeCBItem}
            />

            <div className='d-flex space-between w-100  align-middle'>
                <div className="detailNameFieldWrapper m-0 align-center flex">
                    <input
                        className="detailNameField w-100"
                        name={`subTasks[${subTaskIndex}].items[${detailItemIndex}].name`}
                        placeholder={"Chi tiết việc cần làm..."}
                        value={detail?.name}
                        onChange={handleChangeSubTaskDetail}
                        style={{ textDecoration: detail?.value ? 'line-through' : 'unset' }}
                        autoFocus
                    />
                </div>

                <div className="flex align-center actionContainer">

                    <SubTaskDetailItemTime
                        detail={detail}
                        detailItemIndex={detailItemIndex}
                        subTaskIndex={subTaskIndex}
                    />
                    {(detail?.startTime && detail?.endTime) && (
                        <p className='Member my-0 mr-2 mb-0 p-3'>
                            {formatDate('HH:mm DD/MM', detail?.startTime) + ' - ' + formatDate('HH:mm DD/MM', detail?.endTime)}
                        </p>
                    )}

                    <SubTaskDetailItemDoer
                        detail={detail}
                        detailItemIndex={detailItemIndex}
                        subTaskIndex={subTaskIndex}
                    />
                    {(detail?.staffs && detail?.staffs?.length > 0 && detail?.staffs[0]?.displayName) && (
                        <p className='Member my-0 mr-2 mb-0 p-3'>
                            {detail?.staffs[0]?.displayName}
                        </p>
                    )}

                    <SubTaskDetailItemDelete
                        detail={detail}
                        detailItemIndex={detailItemIndex}
                        subTaskIndex={subTaskIndex}
                    />
                </div>
            </div>



        </div>
    );
}

export default memo(observer(SubTaskDetailItem));






