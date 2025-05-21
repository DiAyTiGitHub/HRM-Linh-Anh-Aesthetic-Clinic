import React, { useState, useEffect, memo } from "react";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { NavLink, useParams } from "react-router-dom";
import EditIcon from "@material-ui/icons/Edit";
import GlobitsTable from "app/common/GlobitsTable";
import { pagingOrganizationBranches } from "../OrganizationBranch/OrganizationBranchService";
import ConstantList from "../../appConfig";
import "react-toastify/dist/ReactToastify.css";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";

function WorkplaceSection(props) {
    const { isManager, isUpdateCurrentOrg } = props;

    const { organizationStore } = useStore();
    const {
        selectedOrganization
    } = organizationStore;

    const [orgBranches, setOrgBranches] = useState([]);

    useEffect(function () {
        async function getDataOrgBranches() {
            const searchObject = {
                pageIndex: 1,
                pageSize: 10000,
                organizationId: selectedOrganization?.id
            };
            const res = await pagingOrganizationBranches(searchObject);
            const data = res.data.content;
            setOrgBranches(data);
        }

        if (selectedOrganization?.id) getDataOrgBranches();
    }, [selectedOrganization?.id]);

    const columns = [
        {
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên địa điểm",
            field: "name",
            align: "left",
        },
        {
            title: "Quốc gia",
            field: "country",
            render: (rowData) => (
                <span>
                    {rowData?.country?.name}
                </span>
            ),
        },
        {
            title: "Tỉnh/Thành phố",
            field: "province",
            render: (rowData) => (
                <span>
                    {rowData?.province?.name}
                </span>
            ),
        },
        {
            title: "Quận/Huyện",
            field: "district",
            render: (rowData) => (
                <span>
                    {rowData?.district?.name}
                </span>
            ),
        },
        {
            title: "Xã/Phường",
            field: "commune",
            render: (rowData) => (
                <span>
                    {rowData?.commune?.name}
                </span>
            ),
        },
        {
            title: "Địa chỉ",
            field: "address",
        },
        {
            title: "SĐT",
            field: "phoneNumber",
        },
    ];

    return (
        <div className="index-card">
            <TabAccordion
                title={"Địa điểm làm việc"}
            >
                <Grid container spacing={2}>
                    {isManager && isUpdateCurrentOrg && (
                        <Grid item xs={12} className="pb-0">
                            <div className="flex flex-end">
                                {/* <strong>Địa điểm làm việc:</strong> */}

                                {isManager && isUpdateCurrentOrg && (
                                    <NavLink to={ConstantList.ROOT_PATH + "organization/organization-branch"} className="flex align-center hyperLink">
                                        <EditIcon />
                                        Cập nhật thông tin địa điểm
                                    </NavLink>
                                )}
                            </div>
                        </Grid>
                    )}

                    <Grid item xs={12}>
                        <GlobitsTable
                            data={orgBranches}
                            columns={columns}
                            nonePagination={true}
                        />
                    </Grid>
                </Grid>

            </TabAccordion>

        </div>
    );
}

export default memo(observer(WorkplaceSection));



function TabAccordion({ children, title, component }) {

    const [expanded, setExpanded] = useState(true);

    return (
        <Accordion
            component="section"
            expanded={expanded}
            onChange={(_, value) => setExpanded(value)}
            className="card accordion-root my-10"
        >
            <AccordionSummary>
                <svg className="accordion-icon" fill="#000000" width="24px" height="24px" viewBox="0 0 24 24" enableBackground="new 0 0 24 24">
                    <g strokeWidth="0"></g>
                    <g strokeLinecap="round" strokeLinejoin="round"></g>
                    <g>
                        <path d="M9.9,17.2c-0.6,0-1-0.4-1-1c0-0.3,0.1-0.5,0.3-0.7l3.5-3.5L9.2,8.5c-0.4-0.4-0.4-1,0-1.4c0.4-0.4,1-0.4,1.4,0l4.2,4.2c0.4,0.4,0.4,1,0,1.4c0,0,0,0,0,0l-4.2,4.2C10.4,17.1,10.1,17.2,9.9,17.2z"></path>
                    </g>
                </svg>
                <p className="accordion-title">{title}</p>
            </AccordionSummary>

            <AccordionDetails className="p-8">
                {children ? children : component ? component : ''}
            </AccordionDetails>
        </Accordion>
    )
}
