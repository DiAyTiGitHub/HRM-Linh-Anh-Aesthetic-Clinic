import { Button, Grid, Box, LinearProgress, Typography } from '@material-ui/core'
import React, { memo } from 'react'
import { Form, Formik, useFormikContext } from 'formik';
import { useState } from 'react';
import ScheduleIcon from '@material-ui/icons/Schedule';
import PersonAddIcon from '@material-ui/icons/PersonAdd';
import DeleteIcon from '@material-ui/icons/Delete';
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import GlobitsAutocomplete from 'app/common/form/GlobitsAutocomplete';
import { v4 as uuid } from 'uuid';
import DatePopup from './TaskPopup/DatePopup';
import { formatDate } from 'app/LocalFunction';
import GlobitsCheckBox from 'app/common/form/GlobitsCheckBox';
import PropTypes from 'prop-types';
import { useTranslation } from 'react-i18next';
import GlobitsAvatar from 'app/common/GlobitsAvatar';
import { observer } from 'mobx-react';

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


function SubTask() {
    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();
    const [itemEdit, setItemEdit] = useState(dataDefaultItemSubTask);
    const [indexSubTaskNew, setIndexSubTaskNew] = useState(-1);
    const [isCreate, setIsCreate] = useState(false);

    function handleAddNewItem(item = { ...dataDefaultItemSubTask, clientId: uuid }, indexSubTask = -1) {
        setItemEdit(item);
        setIndexSubTaskNew(indexSubTask);
    }

    function handleSubFormItemSubTask(value) {
        if (!value?.name) {
            return;
        }
        
        let newListItem = values?.subTasks[indexSubTaskNew]?.items;
        const newItem = { ...value, staffs: value?.staffs ? [value?.staffs] : null }

        if (isCreate) {
            newListItem = Array.isArray(newListItem) ? [...newListItem, newItem] : [newItem];
        } else {
            const indexItem = newListItem.findIndex(e => e?.clientId === itemEdit?.clientId || e?.id === itemEdit?.clientId);
            newListItem[indexItem] = newItem;
        }

        handleAddNewItem();
        setFieldValue(`subTasks[${indexSubTaskNew}].items`, newListItem);
    }

    function handleDeleteItem(indexItem, indexSubTask, item) {
        const newSubTask = values?.subTasks[indexSubTask];
        newSubTask.items = newSubTask?.items?.filter((e, index) => index !== indexItem)
        if (item.value) {
            newSubTask.numberCompleted -= 1
        }
        setFieldValue(`subTasks[${indexSubTask}]`, newSubTask);
    }

    function handleCheckedItem(indexSubTask, indexItem, checked) {
        const newSubTask = values?.subTasks[indexSubTask];

        if (checked) {
            newSubTask.numberCompleted += 1;
        } else {
            newSubTask.numberCompleted -= 1;
        }

        newSubTask.items[indexItem].value = checked;
        setFieldValue(`subTasks[${indexSubTask}]`, newSubTask);
    }

    function renderInputAddItem() {
        return (
            <Formik
                enableReinitialize
                initialValues={itemEdit}
                onSubmit={handleSubFormItemSubTask}
            >
                {({ values: valuesItem }) => (
                    <Form>
                        <Grid container>
                            <Grid item xs={12} style={{ marginBottom: 10 }}>
                                <GlobitsTextField rows={2} multiline name={'name'} />
                            </Grid>
                            <Grid xs={12} lg={4} >
                                <Button className='bgc-primary' style={{ marginRight: "5px" }} type='submit'>{t("task.subTask.add")}</Button>
                                <Button className='bgc-white' onClick={() => handleAddNewItem()} >{t("general.button.cancel")}</Button>
                            </Grid>
                            <Grid xs={12} lg={4} style={{ paddingLeft: '20px' }}>
                                <DatePopup
                                    noEstimateHour
                                    titleButton={(valuesItem?.startTime && valuesItem?.endTime) ? formatDate('HH:mm DD/MM', valuesItem?.startTime) + ' - ' + formatDate('HH:mm DD/MM', valuesItem?.endTime) : ''} />
                            </Grid>
                            <Grid xs={12} lg={4} style={{ padding: '0 10px' }}>
                                <GlobitsAutocomplete
                                    name={'staffs'}
                                    options={Array.isArray(values?.staffs) ? values?.staffs : []}
                                    displayData='displayName'
                                />
                            </Grid>
                        </Grid>
                    </Form>
                )}
            </Formik>
        )
    }

    return (
        Array.isArray(values?.subTasks) && (
            values.subTasks.map((subTask, index) => {
                const percentageCompleted = Array.isArray(subTask?.items) ? subTask?.numberCompleted / subTask?.items?.length * 100 : null;

                return (
                    < >
                        <div key={index}>
                            <div className="d-flex space-between w-100">
                                <p className="m-0" style={{ fontSize: 16 }}>{subTask.name}</p>
                                <Button className="p-0 brc-primary-l2" style={{ fontSize: 16 }}
                                    onClick={() => setFieldValue('subTasks', values.subTasks.filter((e, idx) => idx !== index))}
                                >{t("task.subTask.delete")}</Button>
                            </div>
                            <LinearProgressWithLabel value={percentageCompleted ? percentageCompleted : 0} />
                            {Array.isArray(subTask.items) && (
                                subTask.items.map((item, number) => {
                                    if (item?.clientId === itemEdit?.clientId || item?.id === itemEdit?.clientId) {
                                        return renderInputAddItem();
                                    }
                                    return (
                                        <div key={number} className='checkbox d-flex  w-100 align-middle'>
                                            <GlobitsCheckBox name={`subTasks[${index}].items[${number}].value`} onChange={e => handleCheckedItem(index, number, e.target.checked)} style={{ color: "#5cafe5" }} />
                                            <div className='d-flex space-between w-100  align-middle' onClick={() => {
                                                handleAddNewItem({
                                                    ...item,
                                                    clientId: item?.id ? item?.id : item?.clientId,
                                                    staffs: Array.isArray(item?.staffs) ? item?.staffs[0] : null
                                                }, index);

                                                setIsCreate(false);
                                            }}>
                                                <p style={{ textDecoration: item?.value ? 'line-through' : 'unset' }}>{item?.name}</p>
                                                <div style={{ display: "flex", alignItems: "center" }}>
                                                    {(item?.startTime && item?.endTime) ? (
                                                        <p className='Member' style={{ margin: '0 2px 0 0', padding: 3 }}>{formatDate('HH:mm DD/MM', item?.startTime) + ' - ' + formatDate('HH:mm DD/MM', item?.endTime)}</p>
                                                    ) : (
                                                        <ScheduleIcon style={{ width: 15, margin: '0 5px' }} />
                                                    )}

                                                    {Array.isArray(item?.staffs) && item?.staffs?.length > 0 ? (
                                                        <GlobitsAvatar style={{ width: '20px', height: 20, borderRadius: '50%', margin: '0 3px' }} imgPath={item?.staffs[0]?.imgPath} name={item?.staffs[0]?.displayName} />
                                                    ) : (
                                                        <PersonAddIcon style={{ width: 15, margin: '0 5px' }} />
                                                    )}
                                                    <DeleteIcon style={{ width: 15, margin: '0 5px' }} onClick={() => handleDeleteItem(number, index, item)} />
                                                </div>
                                            </div>

                                        </div>
                                    )
                                })
                            )}
                        </div>
                        {(indexSubTaskNew === index && isCreate) ? (
                            renderInputAddItem()
                        ) : (
                            <Button className="brc-primary-l2" style={{ fontSize: 16, marginTop: "10px", marginBottom: "10px" }}
                                onClick={() => {
                                    handleAddNewItem({ ...dataDefaultItemSubTask, clientId: uuid }, index);
                                    setIsCreate(true);
                                }}
                            >{t("task.subTask.addAItem")}</Button>
                        )}
                    </>
                )
            })
        )
    )
}

export default memo(observer(SubTask));


function LinearProgressWithLabel(props) {
    return (
        <Box display="flex" alignItems="center">
            <Box minWidth={35}>
                <Typography variant="body2" color="textSecondary">{`${Math.round(props.value,)}%`}</Typography>
            </Box>
            <Box width="100%" mr={1}>
                <LinearProgress variant="determinate" {...props} className={Math.round(props.value,) === 100 ? "linecomplete" : "line"} />
            </Box>
        </Box>
    );
}

LinearProgressWithLabel.propTypes = {
    value: PropTypes.number.isRequired,
};