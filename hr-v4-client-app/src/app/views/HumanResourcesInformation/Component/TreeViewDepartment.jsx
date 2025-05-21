import { FormControl , Grid , Icon , IconButton , Input , InputAdornment , Tooltip } from "@material-ui/core";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import BookmarkBorderOutlinedIcon from "@material-ui/icons/BookmarkBorderOutlined";
import SearchIcon from "@material-ui/icons/Search";
import { useStore } from "app/stores";
import { createContext , memo , useContext , useState } from "react";
import { useTranslation } from "react-i18next";

const ExpandIcon = () => (
    <svg fill='#565656' width='9px' height='9px' viewBox='0 0 24.00 24.00'>
        <g strokeWidth='0'></g>
        <g strokeLinecap='round' strokeLinejoin='round' stroke='#CCCCCC' strokeWidth='0.048'></g>
        <g>
            <path
                d='M5.536 21.886a1.004 1.004 0 0 0 1.033-.064l13-9a1 1 0 0 0 0-1.644l-13-9A1 1 0 0 0 5 3v18a1 1 0 0 0 .536.886z'></path>
        </g>
    </svg>
);

const CollapseIcon = () => (
    <svg fill='#000000' width='9px' height='9px' viewBox='0 0 24 24' transform='matrix(-1, 0, 0, -1, 0, 0)rotate(270)'>
        <g strokeWidth='0'></g>
        <g strokeLinecap='round' strokeLinejoin='round'></g>
        <g>
            <path
                d='M5.536 21.886a1.004 1.004 0 0 0 1.033-.064l13-9a1 1 0 0 0 0-1.644l-13-9A1 1 0 0 0 5 3v18a1 1 0 0 0 .536.886z'></path>
        </g>
    </svg>
);

const TreeContext = createContext({
    onNodeSelect:null ,
    selectedNode:null ,
});

const TreeViewDepartment = ({departmentTreeView , onNodeSelect , handleSearch , selectedNode}) => {
    const {departmentStore , departmentV2Store , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    // Nhóm phòng ban theo tổ chức
    const groupedDepartments = new Map();
    departmentTreeView.forEach((dept) => {
        const orgId = dept.organization?.id;
        if (!groupedDepartments.has(orgId)) {
            groupedDepartments.set(orgId , {
                id:dept.organization?.id ,
                name:dept.organization?.name || "Không thuộc đơn vị" ,
                departments:[] ,
            });
        }
        groupedDepartments.get(orgId).departments.push(dept);
    });

    const {handleEditDepartment} = departmentStore;
    const {isAdmin} = hrRoleUtilsStore;

    return (
        <div className='bg-white h-100'>
            {typeof handleSearch === "function" && (
                <FormControl fullWidth className='px-16 pt-12'>
                    <Grid container spacing={2}>
                        <Grid item xs={12} flexend style={{display:"flex" , justifyContent:"flex-end"}}>
                            {(isAdmin) && (
                                <Tooltip title='Thêm phòng ban' placement='top'>
                                    <IconButton
                                        size='small'
                                        onClick={function () {
                                            handleEditDepartment();
                                            departmentV2Store.handleOpenCreateEdit();
                                        }}>
                                        <Icon fontSize='small' color='primary'>
                                            add
                                        </Icon>
                                    </IconButton>
                                </Tooltip>
                            )}
                        </Grid>
                    </Grid>

                    <Input
                        className='search-department'
                        onChange={handleSearch}
                        placeholder='Nhập từ khóa'
                        startAdornment={
                            <InputAdornment>
                                <SearchIcon style={{color:"#a5a5a5"}}/>
                            </InputAdornment>
                        }
                    />
                </FormControl>
            )}

            <TreeContext.Provider value={{onNodeSelect , selectedNode}}>
                {[... groupedDepartments.values()].map((org , index) => (
                    <OrgTree key={index} org={org}/>
                ))}
            </TreeContext.Provider>
        </div>
    );
};

export default memo(TreeViewDepartment);

const OrgTree = ({org}) => {
    const [open , setOpen] = useState(true);
    return (
        <div className='org-container'>
            <li className='tree-item' onClick={() => setOpen((prev) => !prev)} style={{cursor:"pointer"}}>
                <span
                    className={`tree-item-label`}
                    // borderLeft: "1px dashed rgba(0, 0, 0, 0.4)",
                >
                    <p className='tree-item-icon'>{open ? <CollapseIcon/> : <ExpandIcon/>}</p>

                    <p className='flex items-center item-org'>
                        <AccountBalanceIcon style={{fontSize:"1.15rem"}} className='mr-3'/>
                        <span>{org?.name}</span>
                    </p>
                </span>
            </li>

            {open && (
                <ul className='tree-department'>
                    {org?.departments.map((item , index) => (
                        <ItemTree key={index} item={item} sortNumber={item?.hrDepartmentType?.sortNumber}/>
                    ))}
                </ul>
            )}
        </div>
    );
};

const ItemTree = memo(({item , level = 0 , sortNumber}) => {
    const {hrRoleUtilsStore , departmentV2Store} = useStore();
    const {selectedNode , onNodeSelect} = useContext(TreeContext);
    const [open , setOpen] = useState(false);
    const [isHovered , setIsHovered] = useState(false);

    const {handleEditDepartment , handleOpenView} = useStore().departmentStore;

    const isChildren = Array.isArray(item.children) && item.children.length > 0;
    const isSelected = selectedNode === item.id;

    const onClickTreeIcon = (e) => {
        if (isChildren) {
            e.stopPropagation();
            setOpen((prev) => !prev);
        }
    };

    const calcPadding = sortNumber ?? level;

    const onChangeNode = (e) => {
        if (item.id !== selectedNode) {
            onNodeSelect(e , item.id);
        }
    };
    const {isAdmin} = hrRoleUtilsStore;

    return (
        <li className='tree-item' onMouseEnter={() => setIsHovered(true)} onMouseLeave={() => setIsHovered(false)}>
            <span
                onClick={onChangeNode}
                className={`tree-item-label ${isSelected ? "tree-selected" : ""}`}
                // borderLeft: "1px dashed rgba(0, 0, 0, 0.4)",
                style={{paddingLeft:`${calcPadding * 12}px`}}>
                <p className='tree-item-icon' onClick={onClickTreeIcon}>
                    {isChildren ? open ? <CollapseIcon/> : <ExpandIcon/> : <></>}
                </p>

                <p className='flex items-center'>
                    <BookmarkBorderOutlinedIcon style={{fontSize:"1.15rem"}} className='mr-3'/>
                    <span>
                        ({item?.numberOfPositions || 0}) {item.name}
                    </span>
                    {isHovered && (
                        <div className="">
                            <Tooltip
                                arrow
                                placement="top"
                                title={"Xem chi tiết"}
                            >
                                <IconButton
                                    className="ml-4"
                                    size="small"
                                    style={{
                                        position:"absolute" ,
                                        top:0 ,
                                        right:"40px" ,
                                    }}
                                    onClick={() => handleOpenView(item?.id)}
                                >
                                    <Icon fontSize="small" style={{color:"green"}}>
                                        remove_red_eye
                                    </Icon>
                                </IconButton>

                            </Tooltip>
                            {isAdmin && (
                                <IconButton
                                    className='tree-item-edit'
                                    size='small'
                                    style={{
                                        position:"absolute" ,
                                        top:0 ,
                                        right:"12px" ,
                                    }}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        // Gọi hàm chỉnh sửa phòng ban ở đây
                                        handleEditDepartment(item?.id);
                                        departmentV2Store.handleOpenCreateEdit(item?.id)
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            )}
                        </div>
                    )}
                </p>
            </span>

            {isChildren && (
                <ul className={`tree-item-group ${!open && "tree-item-group_close"}`}>
                    {Array.isArray(item.children)
                        ? item.children.map((e , index) => (
                            <ItemTree
                                key={index}
                                item={e}
                                level={level + 1}
                                sortNumber={e?.hrDepartmentType?.sortNumber}
                            />
                        ))
                        : null}
                </ul>
            )}
        </li>
    );
});
