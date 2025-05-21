import { observer } from "mobx-react";
import React , { memo , useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import { Form , Formik } from "formik";
import { Button , ButtonGroup , Grid } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from "@material-ui/icons/FilterList";
import CandidateFilter from "./CandidateFilter";

function CandidateIndexToolbar() {
    const {candidateStore} = useStore();
    const {t} = useTranslation();

    const {
        pagingCandidates ,
        searchObject ,
        handleSetSearchObject ,
        isOpenFilter ,
        toggleFilterPopup
    } = candidateStore;


    async function handleFilter(values) {
        const newSearchObject = {
            ... values ,
            departmentId:values?.department?.id ,
            organizationId:values?.organization?.id ,
            positionTitleId:values?.positionTitle?.id ,
            pageIndex:1 ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingCandidates();
    }


    return (
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({resetForm , values , setFieldValue , setValues}) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <div className='flex justify-between align-center'>
                                        <GlobitsTextField
                                            placeholder='Tìm kiếm theo mã, tên ứng viên...'
                                            name='keyword'
                                            variant='outlined'
                                            notDelay
                                        />

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon className={``}/>}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={
                                                    <FilterListIcon
                                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                    />
                                                }
                                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                onClick={toggleFilterPopup}>
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <CandidateFilter handleFilter={handleFilter}/>
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(CandidateIndexToolbar));
