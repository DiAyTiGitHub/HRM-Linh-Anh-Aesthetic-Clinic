import { Button } from '@material-ui/core';
import React, { memo } from 'react'
import LibraryAddCheckIcon from "@material-ui/icons/LibraryAddCheck";
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Icon, IconButton } from "@material-ui/core";
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import { useFormikContext } from 'formik';
import Popover from '@material-ui/core/Popover';
import CheckIcon from '@material-ui/icons/Check';
import { toast } from 'react-toastify';
import QueueIcon from '@material-ui/icons/Queue';
import { observer } from 'mobx-react';

const dataDefaultSubTask = {
    taskId: null,
    code: null,
    description: null,
    items: [],
    numberCompleted: 0,
}

function SubTaskPopup(props) {
    const { classButton } = props;
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    const [openTask, setOpenTask] = useState();
    const [keyWord, setKeyWord] = useState('');

    function addSubTask() {
        if (keyWord && keyWord?.length > 0) {
            const newSubTask = { ...dataDefaultSubTask, name: keyWord };
            setFieldValue('subTasks', Array.isArray(values?.subTasks) ? [...values?.subTasks, newSubTask] : [newSubTask]);
            setOpenTask(null);
            setKeyWord('');
        } else {
            toast.info("Chưa nhập tiêu đề việc cần làm");
            return;
        }
    }

    return (
        <div>
            <Button
                className={classButton}
                variant="contained"
                startIcon={<LibraryAddCheckIcon />}
                onClick={(event) => setOpenTask(event.currentTarget)}
            >
                {t("task.subTask.title")}
            </Button>

            {
                openTask && (
                    <Popover
                        id={`subtaskPopup`}
                        anchorOrigin={{
                            vertical: 'bottom',
                            horizontal: 'center',
                        }}
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'center',
                        }}
                        anchorEl={openTask}
                        open={Boolean(openTask)}
                        onClose={() => setOpenTask(null)}
                    >
                        <div className='p-8'>
                            <div className='pb-4' style={{ position: 'relative', borderBottom: "1px solid #dee2e6" }}>
                                <p className='m-0' style={{ textAlign: "center" }}>{t("task.subTask.addCheckList")}</p>
                                <IconButton
                                    style={{ position: "absolute", right: "0", top: "0", padding: 0 }}
                                    onClick={() => setOpenTask(null)}
                                >
                                    <Icon color="disabled" title={"Đóng"} fontSize="small" >
                                        close
                                    </Icon>
                                </IconButton>
                            </div>
                            <div className='pt-8' style={{ maxHeight: '250px', overflow: 'auto', width: "280px" }}>
                                <GlobitsTextField
                                    timeOut={0}
                                    name={'nameSubTask'}
                                    value={keyWord}
                                    placeholder="Nhập việc cần làm..."
                                    label={null}
                                    onChange={e => setKeyWord(e.target.value)}
                                    onKeyPress={e => {
                                        if (e.key === "Enter") {
                                            addSubTask();
                                            e.preventDefault();
                                        }
                                    }}
                                />
                                <Button
                                    fullWidth
                                    className='mt-10 text-white bgc-green-d1'
                                    onClick={() => addSubTask()}
                                >
                                    <QueueIcon className="mr-4" />
                                    Thêm việc cần làm
                                </Button>
                            </div>
                        </div>
                    </Popover>
                )
            }
        </div>
    )
}


export default memo(observer(SubTaskPopup));