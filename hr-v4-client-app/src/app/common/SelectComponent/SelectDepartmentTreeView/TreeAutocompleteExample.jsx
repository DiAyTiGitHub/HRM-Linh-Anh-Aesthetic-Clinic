import { Box, Button, Grid, Paper, Typography } from "@material-ui/core";
import { pagingDepartmentPosition } from "app/views/Department/DepartmentService";
import { Form, Formik } from "formik";
import TreeAutocompletePositionSelector from "../SelectPositionTreeView/TreeAutocompletePositionSelector";
// import TreeAutocompletePositionSelector from "./TreeAutocompletePositionSelector";

// Mock API function - Thay thế bằng API thực tế của bạn
// const pagingAllDepartments = async ({ keyword, pageIndex, pageSize }) => {
//     // Giả lập dữ liệu phòng ban (có cấu trúc tree)
//     const mockDepartments = [
//         { id: 1, name: "Ban Giám đốc", parentId: null },
//         { id: 2, name: "Phòng Kỹ thuật", parentId: 1 },
//         { id: 3, name: "Phòng Kinh doanh", parentId: 1 },
//         { id: 4, name: "Team Frontend", parentId: 2 },
//         { id: 5, name: "Team Backend", parentId: 2 },
//         { id: 6, name: "Team Sales", parentId: 3 },
//     ];

//     // Lọc theo keyword nếu có
//     const filtered = keyword
//         ? mockDepartments.filter((dept) => dept.name.toLowerCase().includes(keyword.toLowerCase()))
//         : mockDepartments;

//     // Phân trang (đơn giản)
//     const startIdx = (pageIndex - 1) * pageSize;
//     const content = filtered.slice(startIdx, startIdx + pageSize);

//     return {
//         data: {
//             content,
//             totalPages: Math.ceil(filtered.length / pageSize),
//         },
//     };
// };

const TreeAutocompleteExample = () => {
    // Giá trị khởi tạo có thể là null hoặc object/array tùy chế độ
    const initialValues = {
        department: null, // Single selection (nhận giá trị là object hoặc null)
        departments: [], // Multiple selection (nhận giá trị là array)
    };

    const handleSubmit = (values) => {
        console.log("Submitted values:", values);
        alert(JSON.stringify(values, null, 2));
    };

    return (
        <Box p={3}>
            <Typography variant='h5' gutterBottom>
                TreeAutocompleteSelector Example
            </Typography>
            <Typography paragraph>
                Component này minh họa cách sử dụng TreeAutocompleteSelector với Formik, hỗ trợ cả chế độ chọn đơn
                (single) và chọn nhiều (multiple).
            </Typography>

            <Paper elevation={2} style={{ padding: "20px", marginTop: "20px" }}>
                <Formik enableReinitialize initialValues={initialValues} onSubmit={handleSubmit}>
                    {({ values, setFieldValue }) => (
                        <Form autoComplete='off'>
                            <Grid container spacing={3}>
                                {/* Multiple Selection Example */}
                                <Grid item xs={12}>
                                    <Typography variant='subtitle1' gutterBottom>
                                        Chọn nhiều phòng ban (multiple)
                                    </Typography>
                                    <TreeAutocompletePositionSelector
                                        label='Phòng ban (chọn nhiều)'
                                        name='departments'
                                        api={pagingDepartmentPosition}
                                        multiple
                                        displayName='name'
                                        hasChild
                                        handleChange={(_, value) => {
                                            console.log("value: ", value?.staff);
                                        }}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <Button
                                        type='submit'
                                        variant='contained'
                                        color='primary'
                                        style={{ marginRight: "10px" }}>
                                        Submit
                                    </Button>
                                    <Button
                                        type='button'
                                        variant='outlined'
                                        onClick={() => {
                                            setFieldValue("department", null);
                                            setFieldValue("departments", []);
                                        }}>
                                        Clear All
                                    </Button>
                                </Grid>
                            </Grid>
                        </Form>
                    )}
                </Formik>
            </Paper>

            <Box mt={4}>
                <Typography variant='h6'>Hướng dẫn sử dụng:</Typography>
                <ul>
                    <li>
                        <strong>Chế độ multiple:</strong> Cho phép chọn nhiều items, giá trị trả về là mảng objects
                    </li>
                    <li>
                        <strong>Chế độ single:</strong> Chỉ chọn 1 item, giá trị trả về là object hoặc null
                    </li>
                    <li>
                        <strong>API cần trả về:</strong> Object có cấu trúc{" "}
                        <code>{`{ data: { content: [], totalPages: number } }`}</code>
                    </li>
                    <li>
                        <strong>Props quan trọng:</strong>
                        <ul>
                            <li>
                                <code>displayName</code>: Field name dùng để hiển thị
                            </li>
                            <li>
                                <code>idField</code>: Field dùng làm khóa chính
                            </li>
                            <li>
                                <code>parentIdField</code>: Field thể hiện quan hệ parent-child
                            </li>
                        </ul>
                    </li>
                </ul>
            </Box>
        </Box>
    );
};

export default TreeAutocompleteExample;
