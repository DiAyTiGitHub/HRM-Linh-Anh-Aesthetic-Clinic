import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { Formik, Form } from "formik";
import ObjectSelectorSection from "./ObjectSelectorSection";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import { Grid } from "@material-ui/core";
import { formatDate } from "app/LocalFunction";

function StaffSelectionForm() {
    const { userStore } = useStore();
    const {
        pagingStaff,
        listUsingStaff,
        resetUsingStaffSection,
        setPageSize,
        usingStaffSO,
        totalStaffElements,
        totalStaffPages,
        handleChangeStaffPage,
        handleSetUsingStaffSO
    } = userStore;

    // Define columns for staff selection
    const staffColumns = [
        {
            title: "Mã",
            field: "staffCode",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
        {
            title: "Tên nhân viên",
            field: "displayName",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            render: (value) => value?.birthDate && (<span>{formatDate("DD/MM/YYYY", value?.birthDate)}</span>),
        },
        {
            title: "Tài khoản đang sử dụng",
            field: "username",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
    ];

    // Function to handle after staff selection
    function handleStaffSelected(staff) {
        //console.log("Selected staff:", staff);
        // Additional logic here
    }

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <Formik
                    initialValues={{ staff: null }}
                    onSubmit={(values) => {
                        console.log("Form submitted with values:", values);
                    }}
                >
                    {({ values }) => (
                        <Form>
                            <ObjectSelectorSection
                                name="staff"
                                label="Nhân viên"
                                placeholder="Chưa chọn nhân viên"
                                required={true}
                                fetchDataFunction={pagingStaff}
                                resetDataFunction={resetUsingStaffSection}
                                fetchAutocompleteFunction={pagingStaff}
                                columns={staffColumns}
                                dataList={listUsingStaff}
                                totalElements={totalStaffElements}
                                totalPages={totalStaffPages}
                                handleChangePage={handleChangeStaffPage}
                                setPageSize={setPageSize}
                                searchObject={usingStaffSO}
                                handleSetSearchObject={handleSetUsingStaffSO}
                                popupTitle="Danh sách nhân viên"
                                searchPlaceholder="Tìm kiếm nhân viên, ứng viên..."
                                handleAfterSubmit={handleStaffSelected}
                                getOptionLabel={(option) =>
                                    option?.displayName && option?.staffCode
                                        ? `${option.displayName} - ${option.staffCode}`
                                        : option?.displayName || option?.staffCode || ''
                                }
                                buttonTooltip="Chọn nhân viên"
                            />
                        </Form>
                    )}
                </Formik>
            </Grid>
        </Grid>
    );
}

export default observer(StaffSelectionForm);