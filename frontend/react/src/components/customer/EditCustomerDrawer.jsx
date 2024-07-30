import {
    Button,
    Drawer, DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";;
import {EditCustomerForm} from "./EditCustomerForm.jsx";


const CreateCustomerDrawerForm = ({initialValues, fetchCustomers}) => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    return <>
        <Button
            mt={4}
            rounded={'full'}
            _hover={{
                transform: 'translateY(-2px)',
                boxShadow: 'lg'
            }}
            onClick={onOpen}
        >
            Edit Customer
        </Button>

        <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Edit Customer</DrawerHeader>

                <DrawerBody>
                    <EditCustomerForm
                        initialValues={initialValues}
                        fetchCustomers={fetchCustomers}
                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button
                        colorScheme={"teal"}
                        onClick={onClose}
                    >
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
    </>
}

export default CreateCustomerDrawerForm;