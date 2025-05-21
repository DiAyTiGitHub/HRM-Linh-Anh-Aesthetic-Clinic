import React, { useState, useEffect, memo } from "react";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { Formik } from "formik";
import FilterContent from "./FilterContent";

function TaskTableFilter(props) {
    const { taskStore, staffStore, workingStatusStore } = useStore();
    const {
        currentProject,
        taskTableFilter,
        setTaskTableFilter,
        getOnViewProjectIdList,
        getOnViewProjects,
        needReloadOnJoinStaff,
        setNeedReloadOnJoinStaff
    } = taskStore;



    const { getOnWorkingStaffsOfProject } = staffStore;
    const { getAllWorkingStatus } = workingStatusStore;

    function handleFormSubmit(values) {
        //console.log("wil submit", values);
        setTaskTableFilter(values);

    }

    //load all working status first
    useEffect(function () {
        getAllWorkingStatus();
    }, []);

    //initialize staff who're joining in selected projects
    useEffect(function () {
        if (needReloadOnJoinStaff) {
            getOnWorkingStaffsOfProject(getOnViewProjectIdList());
            setNeedReloadOnJoinStaff(false);
        }

    }, [needReloadOnJoinStaff]);

    return (
        <div className="filter-card" elevation={6}>
            <Formik
                enableReinitialize
                initialValues={taskTableFilter}
                onSubmit={(values) => handleFormSubmit(values)}
            >
                {({ resetForm, values }) => {
                    return (
                        <FilterContent />
                    );

                }}
            </Formik>
        </div>
    );
}

export default memo(observer(TaskTableFilter));

