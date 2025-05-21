import { Grid, Tooltip } from '@material-ui/core'
import React, { memo } from 'react'
import { useFormikContext } from 'formik';
import { observer } from 'mobx-react';
import LocalOfferIcon from "@material-ui/icons/LocalOffer";
import SubTaskItem from './SubTaskItem';
import AddIcon from "@material-ui/icons/Add";

const dataDefaultSubTask = {
    taskId: null,
    code: null,
    description: null,
    items: [],
    id: null,
    name: null
}

function SubTaskContainer() {
    const { values, setFieldValue } = useFormikContext();

    function handleCreateNewSubTask() {
        const newSubTask = structuredClone(dataDefaultSubTask);
        setFieldValue('subTasks', Array.isArray(values?.subTasks) ? [...values?.subTasks, newSubTask] : [newSubTask]);
    }

    return (
        <Grid container spacing={2} className="py-12">
            <Grid item xs={12}>
                <div className="flex justify-left align-center">
                    <LocalOfferIcon className="mr-8" />{" "}
                    <p className="m-0">
                        Việc cần làm:
                    </p>
                    <Tooltip title="Thêm công việc cần làm" placement="top">
                        <button
                            onClick={handleCreateNewSubTask}
                            type="button"
                            className="addMoreSubTaskBtn m-0 px-0 pt-0 pb-2 ml-4"
                        >
                            <AddIcon />
                        </button>
                    </Tooltip>
                </div>
            </Grid>
            <Grid item xs={12} className='py-0 pl-8'>
                {
                    Array.isArray(values?.subTasks) && (
                        values?.subTasks?.map(function (subTask, index) {

                            return (
                                <SubTaskItem
                                    key={index}
                                    subTask={subTask}
                                    subTaskIndex={index}
                                />
                            )
                        })
                    )
                }
            </Grid>
        </Grid>

    )
}

export default memo(observer(SubTaskContainer));


