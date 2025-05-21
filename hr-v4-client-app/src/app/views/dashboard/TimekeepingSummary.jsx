/* eslint-disable react-hooks/exhaustive-deps */
import React, { memo, useEffect, useMemo, useState } from 'react';
import { CalculatedField, Inject, PivotViewComponent } from '@syncfusion/ej2-react-pivotview';
import GlobitsSelectInput from 'app/common/form/GlobitsSelectInput';
import GlobitsBreadcrumb from 'app/common/GlobitsBreadcrumb';
import { Form, Formik } from 'formik';
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import LocalConstants from "app/LocalConstants";
import { toast } from 'react-toastify';
import { getListTimekeepingSummary } from './DashboardService';
import NoteIcon from "@material-ui/icons/Note";
import { useStore } from 'app/stores';
import { useTranslation } from 'react-i18next';
import { observer } from 'mobx-react';
import ImportExcelDialogV2 from './ImportExcelDialogV2';
import DescriptionIcon from '@material-ui/icons/Description';
import PostAddIcon from '@material-ui/icons/PostAdd';
import ImportTimesheetDataPopup from './ImportTimesheetDataPopup';

function TimekeepingSummary() {
    const { timeKeepStore } = useStore();

    const { t } = useTranslation();

    const {
        shouldOpenImportDialog,
        setShouldOpenImportDialog,

        shouldOpenImportDialogV2,
        setShouldOpenImportDialogV2,
    } = timeKeepStore;


    const [currentMonth, setCurrentMonth] = useState(new Date());
    const [pivotObj, setPivotObj] = useState(null);

    const firsDateCurrentMonth = new Date(currentMonth).setDate(1);
    const [listTimekeepingSummary, setListTimekeepingSummary] = useState([]);

    const calculateHours = (startTime, endTime) => {
        const start = new Date(startTime);
        const end = new Date(endTime);

        // Tính số phút giữa startTime và endTime
        const diffInMinutes = (end - start) / (1000 * 60); // tính bằng phút

        // Chuyển đổi phút thành giờ
        return diffInMinutes / 60;
    };

    async function loadTimekeepingSummary() {
        try {
            const { data } = await getListTimekeepingSummary({ monthReport: currentMonth.getMonth() + 1, yearReport: currentMonth.getFullYear() });
            setListTimekeepingSummary(data.length > 0 ? data : []);
        }
        catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra trong quá trình tải dữ liệu");
            setListTimekeepingSummary([])
        }
    }

    useEffect(() => {
        loadTimekeepingSummary();
    }, [currentMonth]);

    const data = useMemo(() => listTimekeepingSummary?.flatMap(item => (
        item.timePeriods.map(period => {
            const startTime = new Date(period.startTime).getTime();
            const endTime = new Date(period.endTime).getTime();

            return ({
                workingDate: item.workingDate,
                staffName: item.staffName,
                shiftWork: period.shiftWorkDto?.name,
                startTime: startTime,
                endTime: endTime,
                totalTime: calculateHours(startTime, endTime),
            })
        })
    )), [listTimekeepingSummary]);

    function handleChangeMonth(month, setFieldValue) {
        const firsDateCurrentMonthToDate = new Date(firsDateCurrentMonth);
        const date = new Date(firsDateCurrentMonthToDate.setMonth(month - 1));
        setCurrentMonth(date);
        setFieldValue('month', date.getMonth() + 1);
        setFieldValue('year', date.getFullYear());
    } 

    function handleExportExcel() {
        try {
            toast.info("Dữ liệu đang được xử lí, vui lòng đợi");
    
            const processData = (data) => {
                return data.map((item) => ({
                    ...item,
                    startTime: new Date(item.startTime),
                    endTime: new Date(item.endTime),
                }));
            };
            pivotObj.dataSourceSettings.dataSource = processData(pivotObj.dataSourceSettings.dataSource);
    
            // Xuất file Excel
            pivotObj.grid.excelExport();
    
            toast.dismiss();
            toast.success("Yêu cầu đã được xử lý!");
        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra trong quá trình xuất dữ liệu, vui lòng thử lại sau");
        }
    }
    

    return (
        <section className="content-index index-card px-16">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Tổng quan" },
                        { name: t('navigation.statisticsTimekeeping') }
                    ]}
                />
            </div>

            <Grid
                container
                spacing={2}
                className="index-card"
            >
                <Grid item xs={12}>
                    <Formik
                        enableReinitialize
                        initialValues={{
                            month: currentMonth.getMonth() + 1,
                            year: currentMonth.getFullYear(),
                        }}
                    >
                        {({ setFieldValue }) => (
                            <Grid container spacing={2} component={Form}>
                                <Grid item xs={12} sm={8}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Tooltip placement="top" title="Xuất báo cáo Excel">
                                            <Button
                                                startIcon={<DescriptionIcon />}
                                                onClick={handleExportExcel}
                                            >
                                                {t('general.button.export_excel')}
                                            </Button>
                                        </Tooltip>

                                        <Tooltip placement="top" title="Nhập dữ liệu mẫu 1">
                                            <Button
                                                startIcon={<PostAddIcon />}
                                                onClick={() => {
                                                    setShouldOpenImportDialog(true);
                                                }}
                                            >
                                                Nhập dữ liệu mẫu 1
                                            </Button>
                                        </Tooltip>

                                        <Tooltip placement="top" title="Nhập dữ liệu mẫu 1">
                                            <Button
                                                startIcon={<PostAddIcon />}
                                                onClick={() => {
                                                    setShouldOpenImportDialogV2(true);
                                                }}
                                            >
                                                Nhập dữ liệu mẫu 2
                                            </Button>
                                        </Tooltip>

                                        {/* <Button
                                className="btn btn-success d-inline-flex"
                                onClick={() => pivotObj.dataSourceSettings.expandAll = true}
                            >
                                {t('general.button.expand')}
                            </Button>

                            <Button
                                className="btn btn-danger d-inline-flex"
                                onClick={() => pivotObj.dataSourceSettings.expandAll = false}
                            >
                                {t('general.button.collapse')}
                            </Button> */}
                                    </ButtonGroup>

                                </Grid>

                                <Grid item xs={12} sm={4}
                                    className="flex items-center justify-end "
                                >
                                    <div style={{ width: '135px' }}>
                                        <GlobitsSelectInput
                                            name={'month'}
                                            options={LocalConstants.LIST_MONTH}
                                            handleChange={({ target }) => handleChangeMonth(target.value, setFieldValue)}
                                        />
                                    </div>

                                    <div style={{ width: '130px' }} className='ml-8'>
                                        <GlobitsSelectInput
                                            name={'year'}
                                            options={LocalConstants.LIST_YEAR}
                                            handleChange={({ target }) => {
                                                setFieldValue('year', target.value)
                                                setCurrentMonth(new Date(currentMonth.setFullYear(target.value)))
                                            }}
                                        />
                                    </div>
                                </Grid>
                            </Grid>
                        )}
                    </Formik>
                </Grid>

                <Grid item xs={12}>
                    <PivotViewComponent
                        height={'100%'}
                        width={'100%'}
                        dataSourceSettings={{
                            dataSource: data,
                            expandAll: true,
                            columns: [
                                { name: 'workingDate' },
                                { name: "shiftWork" },
                            ],
                            rows: [
                                { name: "staffName" }
                            ],
                            formatSettings: [
                                { name: 'workingDate', type: 'date', format: 'dd/MM/yyyy' },
                                { name: 'startTime', type: 'date', format: 'HH:mm' },
                                { name: 'endTime', type: 'date', format: 'HH:mm' },
                            ],
                            values: [
                                { name: "startTime", caption: "Check In", type: 'Min' },
                                { name: "endTime", caption: "Check Out", type: 'Max' },
                                { name: "totalTime", caption: "Tổng số giờ" },
                            ],
                            showColumnSubTotals: false,
                        }}
                        allowCalculatedField={true}
                        ref={d => setPivotObj(d)}
                        allowExcelExport={true}
                    >
                        <Inject services={[CalculatedField]}></Inject>
                    </PivotViewComponent>
                </Grid>
            </Grid>


            {
                shouldOpenImportDialog && (
                    <ImportTimesheetDataPopup
                        open={shouldOpenImportDialog}
                        t={t}
                        handleClose={() => {
                            setShouldOpenImportDialog(false);
                            //loadTimeSheet();
                        }}
                        renewDataFunction={loadTimekeepingSummary}
                    />
                )
            }


            {shouldOpenImportDialogV2 && (
                <ImportExcelDialogV2
                    open={shouldOpenImportDialogV2}
                    t={t}
                    renewDataFunction={loadTimekeepingSummary}
                    handleClose={() => {
                        setShouldOpenImportDialogV2(false);
                        //loadTimeSheet();
                    }}
                />
            )}



        </section>
    )
}

export default memo(observer(TimekeepingSummary));