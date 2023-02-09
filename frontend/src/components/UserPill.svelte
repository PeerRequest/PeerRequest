<script>
    import {Avatar, Button, Dropdown, DropdownDivider, DropdownItem} from "flowbite-svelte";
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

<div class="flex items-center lg:order-2">
    <Button class="!p-1 lg:!pr-2" color="primary" id="avatar_with_name" pill>
        <Avatar class="lg:mr-2" src="/img/avatar-4.jpg"/>
        <span class="hidden md:inline">{current_user.first_name + " " + current_user.last_name}</span>
    </Button>
    <Dropdown class="w-full" inline triggeredBy="#avatar_with_name">
        <div class="px-4 py-2" slot="header">
            <span class="block text-sm text-gray-900 dark:text-white"> {current_user.first_name + " " + current_user.last_name} </span>
            <span class="block flex-wrap text-sm font-medium"> {current_user.email} </span>
        </div>
        <a href="/my-papers">
            <DropdownItem>
                My Papers
            </DropdownItem>
        </a>
        <a href="/my-reviews">
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
