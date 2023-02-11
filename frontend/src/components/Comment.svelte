<script>
    import {onMount} from 'svelte'

    export let comment = null;

    let commentedTime = new Date;
    //TODO USerController
    let user = null;

    let current = new Date()
    onMount(() => {
        setTimeout(function () {
            current = new Date();
        }, 60000);
        commentedTime = new Date(Date.parse(comment.timestamp))
    })

    $: calculateDate = (date) => {
        date = new Date(date);

        // To calculate the time difference of two dates
        let diff = current.getTime() - date.getTime();

        // Calculate difference between the date and now
        let diffDay = (diff / (1000 * 3600 * 24))
        let diffHour = (Math.abs(diff) / 36e5)
        let diffMin = (Math.round((diff / 1000) / 60))
        console.log(diffDay, diffHour, diffMin)

        //return the correct string
        if (diffDay < 0) {
            return Math.round(diffDay) + " days ago"
        }else if (diffDay >= 1 && diffDay < 2) {
            return Math.round(diffDay) + " day ago"
        } else if (diffDay > 1) {
            return Math.round(diffDay) + " days ago"
        }
        if (diffHour >= 1 && diffHour < 2) {
            return Math.round(diffDay) + " hour ago"
        } else if (diffHour > 1) {
            return Math.round(diffHour) + " hours ago"
        }
        if (diffMin >= 1 && diffMin < 2) {
            return Math.round(diffMin) + " minute ago"
        } else if (diffMin > 1) {
            return Math.round(diffMin) + " minutes ago"
        }
        return "just now"
    }
</script>


<main>
    <div class="rounded-lg outline outline-blue-500 mx-4 my-4 max-w-[90vw]">
        <div class="h-1"/>
        {#if comment !== null}
            <h1 class="font-bold flex mx-2">{user}</h1>
            <h2 class="text-xs mx-2 my-2">{calculateDate(commentedTime)}</h2>
            <p class="my-2.5 font-normal text-gray-700 mx-2">{comment.content}</p>
        {/if}
        <div class="h-1"/>
    </div>
</main>