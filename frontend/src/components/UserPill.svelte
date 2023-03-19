<script>
    import {Button, Dropdown, DropdownDivider, DropdownItem} from "flowbite-svelte";
    import {onMount} from "svelte";
    import Cookies from "js-cookie";

    export let current_user = {
        id: "",
        first_name: "",
        last_name: "",
        email: "",
        account_management_url: "",
    };

    onMount(() => {
        // get current user data from cookie
        current_user = JSON.parse(Cookies.get("current-user") ?? "{}")
    })
</script>

<div class="flex items-center lg:order-2 p-3" >
    <Button aria-label="userpill" class="py-1 px-4 h-12 max-w-xl" color="primary" id="avatar_with_name" pill>
        <span class="hidden md:inline">{current_user.first_name + " " + current_user.last_name}</span>
        <span class="md:hidden">{current_user.first_name.toLocaleUpperCase().charAt(0) + current_user.last_name.toLocaleUpperCase().charAt(0)}</span>
    </Button>
    <Dropdown class="w-full" inline triggeredBy="#avatar_with_name">
        <div class="px-4 py-2" slot="header">
            <span class="block text-sm text-gray-900 dark:text-white"> {current_user.first_name + " " + current_user.last_name} </span>
            <span class="block flex-wrap text-sm font-medium"> {current_user.email} </span>
        </div>
        <a aria-label="my_papers" href="/my-papers">
            <DropdownItem>
                My Papers
            </DropdownItem>
        </a>
        <a aria-label="my_reviews" href="/my-reviews">
            <DropdownItem>
                My Reviews
            </DropdownItem>
        </a>
        <DropdownDivider/>
        <a href={current_user.account_management_url} target="_blank">
            <DropdownItem slot="footer">Account Management</DropdownItem>
        </a>
        <a href="/logout/">
            <DropdownItem slot="footer">Sign out</DropdownItem>
        </a>
    </Dropdown>
</div>
