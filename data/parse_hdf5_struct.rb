
ARGF.each do |line|
  data = line.split(":")[1]
  if(data)
    data=data.split(',')
    if(data.size >=4)
    	data = data.reject{|e| (e=~ /.*\..*/ or e.strip.empty?)}.map(&:to_i)
	    if(data.size >= 4)
		    value = 0
		    #puts data.join(' ')
		    data.each_with_index do |e,index| 
				value += (e <<(index*8))
	    	end
		    if value != 0
		    	puts (value - 9223372036854775807)
		    end
    	end
    end
  end
end
